package cc.topicexplorer.plugin.time.actions.getdaterange;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class GetDateRangeCommand extends TableSelectCommand {

	private static final Logger logger = Logger.getLogger(GetDateRangeCommand.class);
	
	@Override
	public void tableExecute(Context context)  {
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		
		try {
			ResultSet dateRangeRS = database.executeQuery("SELECT MIN(TIME$TIME_STAMP) AS MINDATE, MAX(TIME$TIME_STAMP) AS MAXDATE FROM DOCUMENT");
			if(dateRangeRS.next()) {
				pw.print("{\"DateRange\":[" + dateRangeRS.getInt("MINDATE") + "," +  dateRangeRS.getInt("MAXDATE") + "]}");
			}
		} catch (SQLException e) {
			logger.error("Error while getting date range");
		}
		
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
