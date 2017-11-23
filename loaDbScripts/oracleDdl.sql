-- Drop the tables

DROP TABLE ASSESSMENT_HISTORY CASCADE CONSTRAINTS;
DROP TABLE ASSESSMENT_METRIC CASCADE CONSTRAINTS;
DROP TABLE ASSESSMENT_RESULT CASCADE CONSTRAINTS;
DROP TABLE CRITERIA CASCADE CONSTRAINTS;
DROP TABLE DIMENSION CASCADE CONSTRAINTS;
DROP TABLE DIMENSION_WEIGHTING CASCADE CONSTRAINTS;
DROP TABLE LAYER CASCADE CONSTRAINTS;
DROP TABLE LAYER2DIMENSION CASCADE CONSTRAINTS;

-- Now drop the sequences for ID (primary key) creation

DROP SEQUENCE assessment_history_seq;
DROP SEQUENCE assessment_metric_seq;
DROP SEQUENCE assessment_result_seq;
DROP SEQUENCE criteria_seq;
DROP SEQUENCE dimension_seq;
DROP SEQUENCE dimension_weighting_seq;
DROP SEQUENCE layer_seq;

-- Create all tables for the module

CREATE TABLE ASSESSMENT_HISTORY
(
	ASSESSMENT_HISTORY_ID  	NUMBER(5)	NOT NULL,	
	ASSESSMENT_RESULT_ID    NUMBER(5),	
	EPERSON_ID				NUMBER(38),
	ASSESS_VALUE            VARCHAR2(30) 
);


CREATE TABLE ASSESSMENT_METRIC
(
	ASSESSMENT_METRIC_ID  NUMBER(5) NOT NULL,
	CRITERIA_ID           NUMBER(5) NOT NULL,
	DIMENSION_ID          NUMBER(5) NOT NULL,
	LAYER_ID              NUMBER(5) NOT NULL
);


CREATE TABLE ASSESSMENT_RESULT
(
	ASSESSMENT_RESULT_ID  NUMBER(5) 	NOT NULL,
	ASSESSMENT_METRIC_ID  NUMBER(5),		
	ITEM_ID               NUMBER(38), 	
	METRIC_VALUE          VARCHAR2(30) 	
);


CREATE TABLE CRITERIA
(
	CRITERIA_ID    NUMBER(5) 	NOT NULL,
	CRITERIA_NAME  VARCHAR2(50) 
);


CREATE TABLE DIMENSION
(
	DIMENSION_ID    NUMBER(5) 	NOT NULL,
	DIMENSION_NAME  VARCHAR2(50)
);


CREATE TABLE DIMENSION_WEIGHTING
(
	DIMENSION_WEIGHTING_ID  NUMBER(5) 	NOT NULL,
	DIMENSION_ID   			NUMBER(5), 	
	LAYER_ID       			NUMBER(5), 	
	ITEM_ID        			NUMBER(38), 	
	ADMIN_WEIGHT   			VARCHAR2(30), 
	EXPERT_WEIGHT  			NUMBER(5)
);


CREATE TABLE LAYER
(
	LAYER_ID    NUMBER(5) 	NOT NULL,
	LAYER_NAME  VARCHAR2(50)
);


CREATE TABLE LAYER2DIMENSION
(
	DIMENSION_ID  NUMBER(5) NOT NULL,
	LAYER_ID      NUMBER(5) NOT NULL
);


--unique keys
ALTER TABLE ASSESSMENT_HISTORY
    ADD CONSTRAINT UQ_ASSESSMENT_HIST_EPERSON UNIQUE (ASSESSMENT_RESULT_ID, EPERSON_ID);

ALTER TABLE ASSESSMENT_RESULT
	ADD CONSTRAINT UQ_ASSESSMENT_RESULT_BY_ITEM UNIQUE (ASSESSMENT_METRIC_ID, ITEM_ID);
	
ALTER TABLE CRITERIA
	ADD CONSTRAINT UQ_CRITERIA_CRITERIA_NAME UNIQUE (CRITERIA_NAME);

ALTER TABLE DIMENSION
	ADD CONSTRAINT UQ_DIMENSION_DIMENSION_NAME UNIQUE (DIMENSION_NAME);
	
ALTER TABLE DIMENSION_WEIGHTING
	ADD CONSTRAINT UQ_DIMENSION_WEIGHTING_BY_ITEM UNIQUE (DIMENSION_ID, LAYER_ID, ITEM_ID);

ALTER TABLE LAYER
	ADD CONSTRAINT UQ_LAYER_LAYER_NAME UNIQUE (LAYER_NAME);
	
	
--primary keys
ALTER TABLE ASSESSMENT_HISTORY ADD CONSTRAINT PK_ASSESSMENT_HISTORY 
	PRIMARY KEY (ASSESSMENT_HISTORY_ID);

ALTER TABLE ASSESSMENT_METRIC ADD CONSTRAINT PK_ASSESSMENT_METRIC 
	PRIMARY KEY (ASSESSMENT_METRIC_ID);

ALTER TABLE ASSESSMENT_RESULT ADD CONSTRAINT PK_ASSESSMENT_RESULT 
	PRIMARY KEY (ASSESSMENT_RESULT_ID);

ALTER TABLE CRITERIA ADD CONSTRAINT PK_CRITERIA 
	PRIMARY KEY (CRITERIA_ID);

ALTER TABLE DIMENSION ADD CONSTRAINT PK_DIMENSION 
	PRIMARY KEY (DIMENSION_ID);

ALTER TABLE DIMENSION_WEIGHTING ADD CONSTRAINT PK_DIMENSION_WEIGHTING 
	PRIMARY KEY (DIMENSION_WEIGHTING_ID);

ALTER TABLE LAYER ADD CONSTRAINT PK_LAYER 
	PRIMARY KEY (LAYER_ID);

ALTER TABLE LAYER2DIMENSION ADD CONSTRAINT PK_LAYER2DIMENSION 
	PRIMARY KEY (DIMENSION_ID, LAYER_ID);


--foreign keys
ALTER TABLE ASSESSMENT_HISTORY ADD CONSTRAINT FK_ASSESSHIST_ASSESSRESULT 
	FOREIGN KEY (ASSESSMENT_RESULT_ID) REFERENCES ASSESSMENT_RESULT (ASSESSMENT_RESULT_ID);
	
ALTER TABLE ASSESSMENT_METRIC ADD CONSTRAINT FK_CRITERIA_ID 
	FOREIGN KEY (CRITERIA_ID) REFERENCES CRITERIA (CRITERIA_ID);

ALTER TABLE ASSESSMENT_METRIC ADD CONSTRAINT FK_ASSESSMETRIC_LAYER2DIM 
	FOREIGN KEY (DIMENSION_ID, LAYER_ID) REFERENCES LAYER2DIMENSION (DIMENSION_ID, LAYER_ID);

ALTER TABLE ASSESSMENT_RESULT ADD CONSTRAINT FK_ASSESSMENT_METRIC_ID 
	FOREIGN KEY (ASSESSMENT_METRIC_ID) REFERENCES ASSESSMENT_METRIC (ASSESSMENT_METRIC_ID);

ALTER TABLE DIMENSION_WEIGHTING ADD CONSTRAINT FK_DIMWEIGHTING_LAYER2DIM 
	FOREIGN KEY (DIMENSION_ID, LAYER_ID) REFERENCES LAYER2DIMENSION (DIMENSION_ID, LAYER_ID);

ALTER TABLE LAYER2DIMENSION ADD CONSTRAINT FK_DIMENSION 
	FOREIGN KEY (DIMENSION_ID) REFERENCES DIMENSION (DIMENSION_ID);

ALTER TABLE LAYER2DIMENSION ADD CONSTRAINT FK_LAYER 
	FOREIGN KEY (LAYER_ID) REFERENCES LAYER (LAYER_ID);
	
		
CREATE SEQUENCE layer_seq;

CREATE SEQUENCE dimension_seq;

CREATE SEQUENCE criteria_seq;

CREATE SEQUENCE assessment_history_seq;

CREATE SEQUENCE assessment_metric_seq;

CREATE SEQUENCE assessment_result_seq;

CREATE SEQUENCE dimension_weighting_seq;


CREATE OR REPLACE TRIGGER lay_bir
BEFORE INSERT ON LAYER
FOR EACH ROW

BEGIN
	SELECT layer_seq.NEXTVAL
	INTO :new.LAYER_ID
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER dim_bir
BEFORE INSERT ON DIMENSION
FOR EACH ROW

BEGIN
	SELECT dimension_seq.NEXTVAL
	INTO :new.DIMENSION_ID
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER crit_bir
BEFORE INSERT ON CRITERIA
FOR EACH ROW

BEGIN
	SELECT criteria_seq.NEXTVAL
	INTO :new.CRITERIA_ID
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER assess_his_bir
BEFORE INSERT ON ASSESSMENT_HISTORY
FOR EACH ROW

BEGIN
	SELECT assessment_history_seq.NEXTVAL
	INTO :new.ASSESSMENT_HISTORY_ID
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER assess_bir
BEFORE INSERT ON ASSESSMENT_METRIC
FOR EACH ROW

BEGIN
	SELECT assessment_metric_seq.NEXTVAL
	INTO :new.ASSESSMENT_METRIC_ID
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER assess_res_bir
BEFORE INSERT ON ASSESSMENT_RESULT
FOR EACH ROW

BEGIN
	SELECT assessment_result_seq.NEXTVAL
	INTO :new.ASSESSMENT_RESULT_ID
	FROM dual;
END;

/

CREATE OR REPLACE TRIGGER dim_weight_bir
BEFORE INSERT ON DIMENSION_WEIGHTING
FOR EACH ROW

BEGIN
	SELECT dimension_weighting_seq.NEXTVAL
	INTO :new.DIMENSION_WEIGHTING_ID
	FROM dual;
END;

/

COMMIT;
