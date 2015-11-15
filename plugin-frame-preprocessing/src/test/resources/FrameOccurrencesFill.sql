INSERT INTO 
  FRAME$FRAME_OCCURRENCE ( 
DOCUMENT_ID,
TOPIC_ID,
START_POSITION,
END_POSITION,
START_TERM_ID,
END_TERM_ID,
POS_START,
POS_END
		) 
		SELECT 
		  STRAIGHT_JOIN  
    		  fdc.DOCUMENT_ID, 
    		  t_node_start.TOPIC_ID, 
    		  dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT, 
    		  dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT, 
    		  trm_start.TERM_ID, 
    		  trm_end.TERM_ID,  
    		  pos_frame_start.POS, 
    		  pos_frame_end.POS 
    		FROM 
    		  FRAME$DOCUMENT_CHUNK fdc 
    		    join 
    		  DOCUMENT_TERM_TOPIC dtt_start 
    		    on ( 
    		       fdc.DOCUMENT_ID=dtt_start.DOCUMENT_ID and  
    		       fdc.START_POSITION <  dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT  and 
    		       dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT < fdc.END_POSITION 
    		       ) 
    		    join 
    		  DOCUMENT_TERM_TOPIC dtt_end 
    		    on ( 
    		       dtt_start.DOCUMENT_ID = dtt_end.DOCUMENT_ID and 
    		       dtt_start.POSITION_OF_TOKEN_IN_DOCUMENT < dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT and 
    		       dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT < fdc.END_POSITION 
    		       ) 
    		    join 
    		  POS_TYPE pos_leaf_start  
    		    on (dtt_start.WORDTYPE$WORDTYPE = pos_leaf_start.POS) 
    		    join 
    		  POS_TYPE pos_frame_start 
    		    on ( 
    		       pos_frame_start.low <= pos_leaf_start.LOW  and  
    		       pos_leaf_start.HIGH <= pos_frame_start.high 
    		       ) 
    		    join 
    		  POS_TYPE pos_leaf_end  
    		    on (dtt_end.WORDTYPE$WORDTYPE = pos_leaf_end.POS) 
    		    join 
    		  POS_TYPE pos_frame_end 
    		    on ( 
    		       pos_frame_end.low <= pos_leaf_end.LOW  and  
    		       pos_leaf_end.HIGH <= pos_frame_end.high 
    		       ) 
    		    join 
    		  FRAME$FRAMETYPE 
    		    on ( 
    		       pos_frame_start.POS=FRAME$FRAMETYPE.POS_START and 
    		       pos_frame_end.POS=FRAME$FRAMETYPE.POS_END 
    		       ) 
    		    join 
    		  TOPIC t_leaf_start 
    		    on ( 
    		       t_leaf_start.TOPIC_ID=dtt_start.TOPIC_ID 
    		       ) 
    		    join 
    		  TOPIC t_node_start 
    		    on ( 
    		       t_node_start.HIERARCHICAL_TOPIC$START <= t_leaf_start.HIERARCHICAL_TOPIC$START and 
    		       t_leaf_start.HIERARCHICAL_TOPIC$END <= t_node_start.HIERARCHICAL_TOPIC$END 
    		       ) 
    		    join 
    		  TOPIC t_leaf_end 
    		    on ( 
    		       t_leaf_end.TOPIC_ID=dtt_end.TOPIC_ID 
    		       ) 
    		    join 
    		  TOPIC t_node_end 
    		    on ( 
    		       t_node_end.HIERARCHICAL_TOPIC$START <= t_leaf_end.HIERARCHICAL_TOPIC$START and 
    		       t_leaf_end.HIERARCHICAL_TOPIC$END <= t_node_end.HIERARCHICAL_TOPIC$END 
    		       )        
    		    join 
    		  TERM trm_start 
    		    on ( 
    		       trm_start.TERM_NAME=dtt_start.TERM 
    		       ) 
    		    join 
    		  TERM trm_end 
    		    on ( 
    		       trm_end.TERM_NAME=dtt_end.TERM 
    		       ) 
    		where 
    		  dtt_end.POSITION_OF_TOKEN_IN_DOCUMENT < fdc.END_POSITION and 
    		  t_node_end.TOPIC_ID = t_node_start.TOPIC_ID;
