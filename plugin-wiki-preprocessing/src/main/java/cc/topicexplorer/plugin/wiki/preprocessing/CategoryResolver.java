package cc.topicexplorer.plugin.wiki.preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.chain.Context;

import tools.BracketPositions;
import tools.CategoryElement;
import tools.ExtraInformations;
import tools.WikiIDTitlePair;
import wikiParser.SupporterForBothTypes;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;

public class CategoryResolver extends DependencyCommand {

	private wikiParser.Database db;
	private Properties prop;
	public static String categoryFileName = "categories.csv";
	private final HashSet<String> catChilds = new HashSet<String>();
	private wikiParser.SupporterForBothTypes s;
	private BufferedWriter bw;
	private final LinkedList<String> stack = new LinkedList<String>();
	private static final String CATPARENTTEXT = ""; // "has_no_parent";

	@Override
	public void specialExecute(Context context) {

		if (logger != null) {
			logger.info("[ " + getClass() + " ] - " + " resolve categorys from wiki-db");
		}

		CommunicationContext communicationContext = (CommunicationContext) context;
		prop = (Properties) communicationContext.get("properties");

		try {
			init();
			start();
		} catch (IOException e) {
			logger.error("Category resolver caused a problem");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("Wiki_CategoryTreeCreate");
	};

	private void init() throws IOException {
		// eigene DB Zugriff auf Wikidb
		try {

			String outputFolder = prop.getProperty("Wiki_outputFolder");
			File dir = new File(outputFolder);
			if (!dir.exists()) {
				dir.mkdir();
			}

			db = new wikiParser.Database(prop);
			s = new SupporterForBothTypes(db);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFolder + "/"
					+ categoryFileName))));

		} catch (SQLException e) {
			logger.error("Database for category resolver could not be constructed, due to a database error.");
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			logger.error("Database for category resolver could not be constructed, due to a programming error.");
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			logger.error("Database for category resolver could not be constructed, due to a programming error.");
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			logger.error("Database for category resolver could not be constructed, due to a programming error.");
			throw new RuntimeException(e);
		}
	}

	private void start() throws IOException {

		try {
			ResultSet rs;
			WikiIDTitlePair child;

			String sql = "Select page_title , page_latest from page where page_namespace = 14 ;";
			System.out.println(sql);

			rs = db.executeQuery(sql);

			while (rs.next()) {

				child = new WikiIDTitlePair(Integer.valueOf(rs.getString("page_latest")), rs.getString("page_title"));

				// wenn das kind noch nicht untersucht worden ist
				if (!catChilds.contains(getTitleWithUnderscores(child.getWikiTitle()))) {
					getParentCategoriesOfWikiIdTitle(child);
				}

				emptyingStackAndResolveElements();
			}

		} catch (SQLException e) {
			if (logger != null) {
				logger.warn("[ " + getClass() + " ] - " + e.getMessage());
			}
		} finally {
			bw.flush();
			bw.close();
		}
	}

	private void getParentCategoriesOfWikiIdTitle(WikiIDTitlePair child) {

		try {
			String wikiText;
			List<CategoryElement> listOfLinks;
			BracketPositions bp;

			wikiText = s.getWikiTextOnlyWithID(child.getOld_id());
			bp = new BracketPositions(wikiText, child.getOld_id(), child.getWikiTitle());
			listOfLinks = bp.getCategoryLinkList();

			catChilds.add(getTitleWithUnderscores(child.getWikiTitle()));

			for (CategoryElement e : listOfLinks) {

				addToBW(e);

				// wenn noch nicht in hashset kann es auf den stack
				if (!catChilds.contains(getTitleWithUnderscores(e.getText()))) {
					stack.add(getTitleWithUnderscores(ExtraInformations.getTargetWithoutCategoryInformation(e.getText())));
				}
			}

			bp = null;

		} catch (SQLException e) {
			if (logger != null) {
				logger.warn("[ " + getClass() + ".getParentCategoriesOfWikiIdTitle() ] - " + e.getMessage()
						+ ", only subfunction");
			}
		} catch (IOException e) {
			if (logger != null) {
				logger.warn("[ " + getClass() + ".getParentCategoriesOfWikiIdTitle() ] - " + e.getMessage()
						+ ", only subfunction");
			}
		}
	}

	private void addToBW(CategoryElement e) throws IOException {
		this.bw.append(e.getCategroryTreeOutput() + "\n");
	}

	private String getTitleWithUnderscores(String title) {
		if (title.contains(" ")) {
			title = s.fillSpacesWithUnderscores(title);
		}
		return title;
	}

	private void emptyingStackAndResolveElements() {

		while (stack.size() > 0) {
			String e = stack.remove();
			if (!catChilds.contains(e)) {
				getParentCategoriesOfString(e, false);
			}
		}
	}

	private void getParentCategoriesOfString(String category, Boolean trueIfCommesFromSource) {

		try {
			String sql = "SELECT page_title, page_latest FROM page where page_namespace = 14 and page_title like '"
					+ category + "' ";
			ResultSet rs;
			WikiIDTitlePair id_title;

			rs = db.executeQuery(sql);

			if (rs.next()) {

				id_title = s
						.getOldIdAndWikiTitleFromWikiPageIdFromDatabase(Integer.valueOf(rs.getString("page_latest")));

				getParentCategoriesOfWikiIdTitle(id_title);

			} else {
				// for finding cat-pages who has no parent because of failed
				// import or something, perhaps it doesn't matter with fully
				// imported dumps

				if (!trueIfCommesFromSource) {
					CategoryElement ce = new CategoryElement();
					// ce.setOldId(old_id);
					ce.setTitle(category);
					ce.setText(CATPARENTTEXT);

					addToBW(ce);

					catChilds.add(category);

				}
			}
		} catch (NumberFormatException e) {

			if (logger != null) {
				logger.warn("[ " + getClass() + ".getParentCategoriesOfString() ] - " + e.getMessage()
						+ ", only subfunction");
			}

		} catch (SQLException e) {
			if (logger != null) {
				logger.warn("[ " + getClass() + ".getParentCategoriesOfString() ] - " + e.getMessage()
						+ ", only subfunction");
			}
		} catch (IOException e) {
			if (logger != null) {
				logger.warn("[ " + getClass() + ".getParentCategoriesOfString() ] - " + e.getMessage()
						+ ", only subfunction");
			}
		}

	}

	private static Properties forLocalExcetution() throws IOException {

		Properties prop;
		String fileName = "src/test/resources/localwikiconfig.ini";

		File f = new File(fileName);
		if (f.exists()) {
			prop = new Properties();
			// prop.load(this.getClass().getResourceAsStream("/config.ini"));

			FileInputStream fis = new FileInputStream(fileName);

			prop.load(fis);

			return prop;

		} else {
			System.err.print(f.getAbsolutePath() + "\n");
			throw new FileNotFoundException(f + "not found.");
		}

	}

	private void setProperties(Properties prop) {
		this.prop = prop;
	}

	public static void main(String[] args) {

		try {
			CategoryResolver c = new CategoryResolver();
			c.setProperties(CategoryResolver.forLocalExcetution());
			c.init();
			c.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}