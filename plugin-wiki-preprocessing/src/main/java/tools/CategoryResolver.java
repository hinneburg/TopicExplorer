package tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import wikiParser.Database;
import wikiParser.SupporterForBothTypes;

public class CategoryResolver {
	private static final Integer CATPARENT = 0;
	private static final String CATPARENTTEXT = "";
	private LinkedList<String> list = new LinkedList<String>();
	private Database db;
	private LinkedList<String> stack = new LinkedList<String>();

	private StringBuilder sb = new StringBuilder();
	private SupporterForBothTypes s;

	public CategoryResolver(Database db, LinkedList<String> list) {

		this.db = db;
		this.list = list;
		s = new SupporterForBothTypes(db);
		start();
	}

	private void getParentCategoriesOfString(String category) {
		getParentCategoriesOfString(category, false, CATPARENT);
	}

	private void getParentCategoriesOfString(String category, Boolean trueIfCommesFromSource) {
		getParentCategoriesOfString(category, trueIfCommesFromSource, CATPARENT);
	}

	private void getParentCategoriesOfString(String category, Boolean trueIfCommesFromSource, Integer old_id) {

		String sql = "SELECT * FROM page where page_namespace = 14 and page_title like '" + category + "' ";
		ResultSet rs;
		WikiIDTitlePair id_title;
		String wikiText;
		List<CategoryElement> listOfLinks;
		BracketPositions bp;

		try {
			rs = db.executeQuery(sql);
			// System.out.println(sql);

			if (rs.next()) {

				id_title = s
						.getOldIdAndWikiTitleFromWikiPageIdFromDatabase(Integer.valueOf(rs.getString("page_latest")));
				wikiText = s.getWikiTextOnlyWithID(id_title.getOld_id());
				bp = new BracketPositions(wikiText, id_title.getOld_id(), id_title.getWikiTitle());
				// listOfLinks = bp.getCategoryLinkList();
				listOfLinks = bp.getCategoryLinkList();

				for (CategoryElement e : listOfLinks) {
					sb.append(e.getInfosSeparatedInColumns() + "\n");

					stack.add(ExtraInformations.getTargetWithoutCategoryInformation(e.getText()));
				}

			} else {
				// for finding cat-pages who has no parent because of failed
				// import or something, perhaps it doesn't matter with fully
				// imported dumps
				if (!trueIfCommesFromSource) {
					CategoryElement ce = new CategoryElement();
					ce.setOldId(old_id);
					ce.setTitle(category);
					ce.setText(CATPARENTTEXT);

					sb.append(ce.getInfosSeparatedInColumns() + "\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			bp = null;
			listOfLinks = null;
			wikiText = null;
		}

	}

	private void removeFirstElementFromStackAndResolve() {

		String e = stack.remove();

		if (e.contains(" ")) {
			e = s.fillSpacesWithUnderscores(e);
		}
		getParentCategoriesOfString(e);
	}

	public String start() {

		for (String cat : list) {
			getParentCategoriesOfString(cat, true);
		}

		while (stack.size() > 0) {
			removeFirstElementFromStackAndResolve();
		}

		System.out.println(sb.toString());
		return sb.toString();
	}
}
