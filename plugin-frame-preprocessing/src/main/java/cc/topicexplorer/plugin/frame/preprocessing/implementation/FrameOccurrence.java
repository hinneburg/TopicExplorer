package cc.topicexplorer.plugin.frame.preprocessing.implementation;

import com.google.common.base.Joiner;

import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence.tbl.FRAME$FRAME_OCCURRENCE;

public class FrameOccurrence {
    public static final class tbl {
    	public static final class FRAME$FRAME_OCCURRENCE {
    		public static final String name = FRAME$FRAME_OCCURRENCE.class.getSimpleName();
    		public static final String engine ="INNODB";
    		public static final String list_of_col_declarations = Joiner.on(",").join(
    				DOCUMENT_ID.declaration,
    				TOPIC_ID.declaration,
    				START_POSITION.declaration,
    				END_POSITION.declaration,
    				START_TERM_ID.declaration,
    				END_TERM_ID.declaration,
    				POS_START.declaration,
    				POS_END.declaration
    				);
    		public static final String list_of_col_names = Joiner.on(",").join(
    				DOCUMENT_ID.col,
    				TOPIC_ID.col,
    				START_POSITION.col,
    				END_POSITION.col,
    				START_TERM_ID.col,
    				END_TERM_ID.col,
    				POS_START.col,
    				POS_END.col
    				);    		
    		// @formatter:off
    		public static final String create_stmt =
    				"CREATE TABLE " + 
    				    name + " " +
    				   "(" +
    				   list_of_col_declarations +
    				   ") ENGINE "+engine;
    		// @formatter:on
    		public static final class DOCUMENT_ID {
    			public static final String col = DOCUMENT_ID.class.getSimpleName();
    			public static final String type = "INTEGER(11)";
    			public static final String constraint = "NOT NULL";
    			public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
    			};
    		public static final class TOPIC_ID {
        		public static final String col = TOPIC_ID.class.getSimpleName();
        		public static final String type = "INTEGER(11)";
        		public static final String constraint = "NOT NULL";
        		public static final String declaration = Joiner.on(" ").join(TOPIC_ID.col, TOPIC_ID.type, TOPIC_ID.constraint);    			
        		};
        	public static final class START_POSITION {
            	public static final String col = START_POSITION.class.getSimpleName();
            	public static final String type = "INTEGER(11)";
            	public static final String constraint = "NOT NULL";
            	public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
            	};
           	public static final class END_POSITION {
               	public static final String col = END_POSITION.class.getSimpleName();
               	public static final String type = "INTEGER(11)";
               	public static final String constraint = "NOT NULL";
               	public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
               	};
           	public static final class START_TERM_ID {
               	public static final String col = START_TERM_ID.class.getSimpleName();
               	public static final String type = "INTEGER(11)";
               	public static final String constraint = "NOT NULL";
               	public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
              	};
          	public static final class END_TERM_ID {
               	public static final String col = END_TERM_ID.class.getSimpleName();
               	public static final String type = "INTEGER(11)";
               	public static final String constraint = "NOT NULL";
               	public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
              	};
           	public static final class POS_START {
               	public static final String col = POS_START.class.getSimpleName();
               	public static final String type = "INTEGER UNSIGNED";
               	public static final String constraint = "NOT NULL";
               	public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
              	};
           	public static final class POS_END {
               	public static final String col = POS_END.class.getSimpleName();
               	public static final String type = "INTEGER UNSIGNED";
               	public static final String constraint = "NOT NULL";
              	public static final String declaration = Joiner.on(" ").join(col, type, constraint);    			
               	};
    	};
    	
    };

	
	public static final String getCreateTableStatement() {
		return FRAME$FRAME_OCCURRENCE.create_stmt;
	}

	public static final String getInsertTableStatement(){
		// @formatter:off
		return "INSERT INTO "+
        "  " + FRAME$FRAME_OCCURRENCE.name + " ( " +
                    FRAME$FRAME_OCCURRENCE.list_of_col_names +
        "		) " +
        "		SELECT " +
        "		  STRAIGHT_JOIN  " +
        "    		  fdc.DOCUMENT_ID, " +
        "    		  t_node_start.TOPIC_ID, " +
        "    		  dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT, " +
        "    		  dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT, " +
        "    		  trm_start.TERM_ID, " +
        "    		  trm_end.TERM_ID,  " +
        "    		  pos_frame_start.POS, " +
        "    		  pos_frame_end.POS " +
        "    		FROM " +
        "    		  FRAME$DOCUMENT_CHUNK fdc " +
        "    		    join " +
        "    		  DOCUMENT_TERM_TOPIC dtt_start " +
        "    		    on ( " +
        "    		       fdc.DOCUMENT_ID=dtt_start.DOCUMENT_ID and  " +
        "    		       fdc.START_POSITION <  dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT  and " +
        "    		       dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT < fdc.END_POSITION " +
        "    		       ) " +
        "    		    join " +
        "    		  DOCUMENT_TERM_TOPIC dtt_end " +
        "    		    on ( " +
        "    		       dtt_start.DOCUMENT_ID = dtt_end.DOCUMENT_ID and " +
        "    		       dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT < dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT and " +
        "    		       dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT < fdc.END_POSITION " +
        "    		       ) " +
        "    		    join " +
        "    		  POS_TYPE pos_leaf_start  " +
        "    		    on (dtt_start.WORDTYPE$WORDTYPE = pos_leaf_start.POS) " +
        "    		    join " +
        "    		  POS_TYPE pos_frame_start " +
        "    		    on ( " +
        "    		       pos_frame_start.low <= pos_leaf_start.LOW  and  " +
        "    		       pos_leaf_start.HIGH <= pos_frame_start.high " +
        "    		       ) " +
        "    		    join " +
        "    		  POS_TYPE pos_leaf_end  " +
        "    		    on (dtt_end.WORDTYPE$WORDTYPE = pos_leaf_end.POS) " +
        "    		    join " +
        "    		  POS_TYPE pos_frame_end " +
        "    		    on ( " +
        "    		       pos_frame_end.low <= pos_leaf_end.LOW  and  " +
        "    		       pos_leaf_end.HIGH <= pos_frame_end.high " +
        "    		       ) " +
        "    		    join " +
        "    		  FRAME$FRAMETYPE " +
        "    		    on ( " +
        "    		       pos_frame_start.POS=FRAME$FRAMETYPE.POS_START and " +
        "    		       pos_frame_end.POS=FRAME$FRAMETYPE.POS_END " +
        "    		       ) " +
        "    		    join " +
        "    		  TOPIC t_leaf_start " +
        "    		    on ( " +
        "    		       t_leaf_start.TOPIC_ID=dtt_start.TOPIC_ID " +
        "    		       ) " +
        "    		    join " +
        "    		  TOPIC t_node_start " +
        "    		    on ( " +
        "    		       t_node_start.HIERARCHICAL_TOPIC$START <= t_leaf_start.HIERARCHICAL_TOPIC$START and " +
        "    		       t_leaf_start.HIERARCHICAL_TOPIC$END <= t_node_start.HIERARCHICAL_TOPIC$END " +
        "    		       ) " +
        "    		    join " +
        "    		  TOPIC t_leaf_end " +
        "    		    on ( " +
        "    		       t_leaf_end.TOPIC_ID=dtt_end.TOPIC_ID " +
        "    		       ) " +
        "    		    join " +
        "    		  TOPIC t_node_end " +
        "    		    on ( " +
        "    		       t_node_end.HIERARCHICAL_TOPIC$START <= t_leaf_end.HIERARCHICAL_TOPIC$START and " +
        "    		       t_leaf_end.HIERARCHICAL_TOPIC$END <= t_node_end.HIERARCHICAL_TOPIC$END " +
        "    		       )        " +
        "    		    join " +
        "    		  TERM trm_start " +
        "    		    on ( " +
        "    		       trm_start.TERM_NAME=dtt_start.TERM " +
        "    		       ) " +
        "    		    join " +
        "    		  TERM trm_end " +
        "    		    on ( " +
        "    		       trm_end.TERM_NAME=dtt_end.TERM " +
        "    		       ) " +
        "    		where " +
        "    		  dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT < fdc.END_POSITION and " +
        "    		  t_node_end.TOPIC_ID = t_node_start.TOPIC_ID;"
;
// @formatter:on
	}
}

