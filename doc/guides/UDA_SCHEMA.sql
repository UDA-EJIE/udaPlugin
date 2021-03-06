--------------------------------------------------------
-- Archivo creado  - martes-noviembre-23-2010   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table LINEITEM
--------------------------------------------------------

  CREATE TABLE "UDA"."LINEITEM" 
   (	"QUANTITY" NUMBER(10,0), 
	"ITEMID" NUMBER(10,0), 
	"ORDERID" NUMBER(10,0), 
	"VENDORPARTNUMBER" NUMBER(38,0)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ORDERING
--------------------------------------------------------

  CREATE TABLE "UDA"."ORDERING" 
   (	"ORDERID" NUMBER(10,0), 
	"LASTUPDATE" TIMESTAMP (6), 
	"STATUS" CHAR(1 BYTE), 
	"SHIPMENTINFO" VARCHAR2(255 BYTE), 
	"DISCOUNT" NUMBER(10,0)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PART
--------------------------------------------------------

  CREATE TABLE "UDA"."PART" 
   (	"DESCRIPTION" VARCHAR2(255 BYTE), 
	"SPECIFICATION" CLOB, 
	"REVISIONDATE" DATE, 
	"DRAWING" BLOB, 
	"REVISION" NUMBER(10,0), 
	"PARTNUMBER" VARCHAR2(255 BYTE), 
	"BOMREVISION" NUMBER(10,0), 
	"BOMPARTNUMBER" VARCHAR2(255 BYTE)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" 
 LOB ("SPECIFICATION") STORE AS (
  TABLESPACE "USERS" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10
  NOCACHE LOGGING 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)) 
 LOB ("DRAWING") STORE AS (
  TABLESPACE "USERS" ENABLE STORAGE IN ROW CHUNK 8192 PCTVERSION 10
  NOCACHE LOGGING 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)) ;
--------------------------------------------------------
--  DDL for Table PAYMENT
--------------------------------------------------------

  CREATE TABLE "UDA"."PAYMENT" 
   (	"PAYMENTID" NUMBER(38,0), 
	"DESCRIPTION" VARCHAR2(255 BYTE), 
	"MAXAMMOUNT" NUMBER(38,0), 
	"MINAMMOUNT" NUMBER(38,0), 
	"METHOD" VARCHAR2(255 BYTE)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PRESENT
--------------------------------------------------------

  CREATE TABLE "UDA"."PRESENT" 
   (	"ORDERID" NUMBER(10,0), 
	"DESCRIPTION" VARCHAR2(500 BYTE), 
	"NAME" VARCHAR2(50 BYTE), 
	"BARCODE" VARCHAR2(20 BYTE), 
	"AVAILABLE" NUMBER(1,0) DEFAULT 0
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table VENDOR
--------------------------------------------------------

  CREATE TABLE "UDA"."VENDOR" 
   (	"VENDORID" NUMBER(10,0), 
	"PHONE" VARCHAR2(255 BYTE), 
	"VENDORNAME" VARCHAR2(255 BYTE), 
	"ADDRESS" VARCHAR2(255 BYTE), 
	"CONTACT" VARCHAR2(255 BYTE)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table VENDOR_PART
--------------------------------------------------------

  CREATE TABLE "UDA"."VENDOR_PART" 
   (	"VENDORPARTNUMBER" NUMBER(38,0), 
	"PRICE" NUMBER(38,0), 
	"DESCRIPTION" VARCHAR2(255 BYTE), 
	"PARTREVISION" NUMBER(10,0), 
	"PARTNUMBER" VARCHAR2(255 BYTE), 
	"VENDORID" NUMBER(10,0)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table VENDOR_PAYMENT
--------------------------------------------------------

  CREATE TABLE "UDA"."VENDOR_PAYMENT" 
   (	"VENDOR_ID" NUMBER(10,0), 
	"PAYMENT_ID" NUMBER(38,0)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
REM INSERTING into UDA.LINEITEM
Insert into UDA.LINEITEM (QUANTITY,ITEMID,ORDERID,VENDORPARTNUMBER) values (3,1,1111,1);
Insert into UDA.LINEITEM (QUANTITY,ITEMID,ORDERID,VENDORPARTNUMBER) values (5,2,1111,2);
Insert into UDA.LINEITEM (QUANTITY,ITEMID,ORDERID,VENDORPARTNUMBER) values (7,5,1111,3);
Insert into UDA.LINEITEM (QUANTITY,ITEMID,ORDERID,VENDORPARTNUMBER) values (1,7,4312,5);
Insert into UDA.LINEITEM (QUANTITY,ITEMID,ORDERID,VENDORPARTNUMBER) values (3,5,4312,7);
Insert into UDA.LINEITEM (QUANTITY,ITEMID,ORDERID,VENDORPARTNUMBER) values (15,1,4312,1);
REM INSERTING into UDA.ORDERING
Insert into UDA.ORDERING (ORDERID,LASTUPDATE,STATUS,SHIPMENTINFO,DISCOUNT) values (1111,to_timestamp('09/11/10 20:38:45,276000000','DD/MM/RR HH24:MI:SS,FF'),'N','333 New Court, New City, CA 90000',10);
Insert into UDA.ORDERING (ORDERID,LASTUPDATE,STATUS,SHIPMENTINFO,DISCOUNT) values (4312,to_timestamp('09/11/10 20:38:45,307000000','DD/MM/RR HH24:MI:SS,FF'),'N','666 Old Court, Old City, NY 60000',0);
REM INSERTING into UDA.PART
Insert into UDA.PART (DESCRIPTION,SPECIFICATION,REVISIONDATE,DRAWING,REVISION,PARTNUMBER,BOMREVISION,BOMPARTNUMBER) values ('ABC PART', EMPTY_CLOB(),to_timestamp('09/11/10 00:00:00,000000000','DD/MM/RR HH24:MI:SS,FF'), EMPTY_BLOB(),1,'1234-5678-01',7,'SDFG-ERTY-BN');
Insert into UDA.PART (DESCRIPTION,SPECIFICATION,REVISIONDATE,DRAWING,REVISION,PARTNUMBER,BOMREVISION,BOMPARTNUMBER) values ('DEF PART', EMPTY_CLOB(),to_timestamp('09/11/10 00:00:00,000000000','DD/MM/RR HH24:MI:SS,FF'), EMPTY_BLOB(),2,'9876-4321-02',7,'SDFG-ERTY-BN');
Insert into UDA.PART (DESCRIPTION,SPECIFICATION,REVISIONDATE,DRAWING,REVISION,PARTNUMBER,BOMREVISION,BOMPARTNUMBER) values ('GHI PART', EMPTY_CLOB(),to_timestamp('09/11/10 00:00:00,000000000','DD/MM/RR HH24:MI:SS,FF'), EMPTY_BLOB(),3,'5456-6789-03',7,'SDFG-ERTY-BN');
Insert into UDA.PART (DESCRIPTION,SPECIFICATION,REVISIONDATE,DRAWING,REVISION,PARTNUMBER,BOMREVISION,BOMPARTNUMBER) values ('XYZ PART', EMPTY_CLOB(),to_timestamp('09/11/10 00:00:00,000000000','DD/MM/RR HH24:MI:SS,FF'), EMPTY_BLOB(),5,'ABCD-XYZW-FF',7,'SDFG-ERTY-BN');
Insert into UDA.PART (DESCRIPTION,SPECIFICATION,REVISIONDATE,DRAWING,REVISION,PARTNUMBER,BOMREVISION,BOMPARTNUMBER) values ('BOM PART', EMPTY_CLOB(),to_timestamp('09/11/10 00:00:00,000000000','DD/MM/RR HH24:MI:SS,FF'), EMPTY_BLOB(),7,'SDFG-ERTY-BN',null,null);
REM INSERTING into UDA.PAYMENT
Insert into UDA.PAYMENT (PAYMENTID,DESCRIPTION,MAXAMMOUNT,MINAMMOUNT,METHOD) values (1,'Credit Card in Euros',99000,100,'Credit Card');
Insert into UDA.PAYMENT (PAYMENTID,DESCRIPTION,MAXAMMOUNT,MINAMMOUNT,METHOD) values (2,'Debit Card in Euros',6600,100,'Debit Card');
Insert into UDA.PAYMENT (PAYMENTID,DESCRIPTION,MAXAMMOUNT,MINAMMOUNT,METHOD) values (3,'Check in Euros',9999999,0,'Check');
Insert into UDA.PAYMENT (PAYMENTID,DESCRIPTION,MAXAMMOUNT,MINAMMOUNT,METHOD) values (4,'Cash in Euros',99000,0,'Cash');
Insert into UDA.PAYMENT (PAYMENTID,DESCRIPTION,MAXAMMOUNT,MINAMMOUNT,METHOD) values (5,'Paypal international Payment',2354,10,'PayPal');
REM INSERTING into UDA.PRESENT
Insert into UDA.PRESENT (ORDERID,DESCRIPTION,NAME,BARCODE,AVAILABLE) values (1111,'Ultimate tablet','Apple iPad','1gr56ynw43u789iknj2r',1);
Insert into UDA.PRESENT (ORDERID,DESCRIPTION,NAME,BARCODE,AVAILABLE) values (4312,'3D Television','Panasonic Viera','u9r56ynw421789ikng3r',0);
REM INSERTING into UDA.VENDOR
Insert into UDA.VENDOR (VENDORID,PHONE,VENDORNAME,ADDRESS,CONTACT) values (100,'WidgetCorp','111 Main St., Anytown, KY 99999','Mr. Jones','888-777-9999');
Insert into UDA.VENDOR (VENDORID,PHONE,VENDORNAME,ADDRESS,CONTACT) values (200,'Gadget, Inc.','123 State St., Sometown, MI 88888','Mrs. Smith','866-345-6789');
REM INSERTING into UDA.VENDOR_PART
Insert into UDA.VENDOR_PART (VENDORPARTNUMBER,PRICE,DESCRIPTION,PARTREVISION,PARTNUMBER,VENDORID) values (1,100,'PART1',1,'1234-5678-01',100);
Insert into UDA.VENDOR_PART (VENDORPARTNUMBER,PRICE,DESCRIPTION,PARTREVISION,PARTNUMBER,VENDORID) values (2,10,'PART2',2,'9876-4321-02',200);
Insert into UDA.VENDOR_PART (VENDORPARTNUMBER,PRICE,DESCRIPTION,PARTREVISION,PARTNUMBER,VENDORID) values (3,76,'PART3',3,'5456-6789-03',200);
Insert into UDA.VENDOR_PART (VENDORPARTNUMBER,PRICE,DESCRIPTION,PARTREVISION,PARTNUMBER,VENDORID) values (5,55,'PART4',5,'ABCD-XYZW-FF',100);
Insert into UDA.VENDOR_PART (VENDORPARTNUMBER,PRICE,DESCRIPTION,PARTREVISION,PARTNUMBER,VENDORID) values (7,346,'PART5',7,'SDFG-ERTY-BN',100);
REM INSERTING into UDA.VENDOR_PAYMENT
Insert into UDA.VENDOR_PAYMENT (VENDOR_ID,PAYMENT_ID) values (100,1);
Insert into UDA.VENDOR_PAYMENT (VENDOR_ID,PAYMENT_ID) values (100,2);
Insert into UDA.VENDOR_PAYMENT (VENDOR_ID,PAYMENT_ID) values (100,3);
Insert into UDA.VENDOR_PAYMENT (VENDOR_ID,PAYMENT_ID) values (200,3);
Insert into UDA.VENDOR_PAYMENT (VENDOR_ID,PAYMENT_ID) values (200,4);
Insert into UDA.VENDOR_PAYMENT (VENDOR_ID,PAYMENT_ID) values (200,5);
--------------------------------------------------------
--  DDL for Index SYS_C005189
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005189" ON "UDA"."LINEITEM" ("ITEMID", "ORDERID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005176
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005176" ON "UDA"."ORDERING" ("ORDERID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005186
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005186" ON "UDA"."PART" ("REVISION", "PARTNUMBER") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005174
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005174" ON "UDA"."PAYMENT" ("PAYMENTID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005181
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005181" ON "UDA"."PRESENT" ("ORDERID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005191
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005191" ON "UDA"."VENDOR" ("VENDORID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005183
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005183" ON "UDA"."VENDOR_PART" ("VENDORPARTNUMBER") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UNQ_VENDOR_PART_0
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."UNQ_VENDOR_PART_0" ON "UDA"."VENDOR_PART" ("PARTNUMBER", "PARTREVISION") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C005172
--------------------------------------------------------

  CREATE UNIQUE INDEX "UDA"."SYS_C005172" ON "UDA"."VENDOR_PAYMENT" ("VENDOR_ID", "PAYMENT_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table LINEITEM
--------------------------------------------------------

  ALTER TABLE "UDA"."LINEITEM" MODIFY ("ITEMID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."LINEITEM" MODIFY ("ORDERID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."LINEITEM" ADD PRIMARY KEY ("ITEMID", "ORDERID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ORDERING
--------------------------------------------------------

  ALTER TABLE "UDA"."ORDERING" MODIFY ("ORDERID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."ORDERING" ADD PRIMARY KEY ("ORDERID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PART
--------------------------------------------------------

  ALTER TABLE "UDA"."PART" MODIFY ("REVISION" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PART" MODIFY ("PARTNUMBER" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PART" ADD PRIMARY KEY ("REVISION", "PARTNUMBER")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PAYMENT
--------------------------------------------------------

  ALTER TABLE "UDA"."PAYMENT" MODIFY ("PAYMENTID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PAYMENT" ADD PRIMARY KEY ("PAYMENTID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PRESENT
--------------------------------------------------------

  ALTER TABLE "UDA"."PRESENT" MODIFY ("ORDERID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PRESENT" MODIFY ("NAME" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PRESENT" MODIFY ("BARCODE" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PRESENT" MODIFY ("AVAILABLE" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."PRESENT" ADD PRIMARY KEY ("ORDERID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table VENDOR
--------------------------------------------------------

  ALTER TABLE "UDA"."VENDOR" MODIFY ("VENDORID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."VENDOR" ADD PRIMARY KEY ("VENDORID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table VENDOR_PART
--------------------------------------------------------

  ALTER TABLE "UDA"."VENDOR_PART" MODIFY ("VENDORPARTNUMBER" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."VENDOR_PART" ADD PRIMARY KEY ("VENDORPARTNUMBER")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "UDA"."VENDOR_PART" ADD CONSTRAINT "UNQ_VENDOR_PART_0" UNIQUE ("PARTNUMBER", "PARTREVISION")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table VENDOR_PAYMENT
--------------------------------------------------------

  ALTER TABLE "UDA"."VENDOR_PAYMENT" MODIFY ("VENDOR_ID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."VENDOR_PAYMENT" MODIFY ("PAYMENT_ID" NOT NULL ENABLE);
 
  ALTER TABLE "UDA"."VENDOR_PAYMENT" ADD PRIMARY KEY ("VENDOR_ID", "PAYMENT_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table LINEITEM
--------------------------------------------------------

  ALTER TABLE "UDA"."LINEITEM" ADD CONSTRAINT "FK_LINEITEM_ORDERID" FOREIGN KEY ("ORDERID")
	  REFERENCES "UDA"."ORDERING" ("ORDERID") ENABLE;
 
  ALTER TABLE "UDA"."LINEITEM" ADD CONSTRAINT "FK_LINEITEM_VENDORPARTNUMBER" FOREIGN KEY ("VENDORPARTNUMBER")
	  REFERENCES "UDA"."VENDOR_PART" ("VENDORPARTNUMBER") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PART
--------------------------------------------------------

  ALTER TABLE "UDA"."PART" ADD CONSTRAINT "FK_PART_BOMREVISION" FOREIGN KEY ("BOMREVISION", "BOMPARTNUMBER")
	  REFERENCES "UDA"."PART" ("REVISION", "PARTNUMBER") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PRESENT
--------------------------------------------------------

  ALTER TABLE "UDA"."PRESENT" ADD CONSTRAINT "FK_PRESENT_ORDERID" FOREIGN KEY ("ORDERID")
	  REFERENCES "UDA"."ORDERING" ("ORDERID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VENDOR_PART
--------------------------------------------------------

  ALTER TABLE "UDA"."VENDOR_PART" ADD CONSTRAINT "FK_VENDOR_PART_PARTREVISION" FOREIGN KEY ("PARTREVISION", "PARTNUMBER")
	  REFERENCES "UDA"."PART" ("REVISION", "PARTNUMBER") ENABLE;
 
  ALTER TABLE "UDA"."VENDOR_PART" ADD CONSTRAINT "FK_VENDOR_PART_VENDORID" FOREIGN KEY ("VENDORID")
	  REFERENCES "UDA"."VENDOR" ("VENDORID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VENDOR_PAYMENT
--------------------------------------------------------

  ALTER TABLE "UDA"."VENDOR_PAYMENT" ADD CONSTRAINT "FK_VENDOR_PAYMENT_PAYMENT_ID" FOREIGN KEY ("PAYMENT_ID")
	  REFERENCES "UDA"."PAYMENT" ("PAYMENTID") ENABLE;
 
  ALTER TABLE "UDA"."VENDOR_PAYMENT" ADD CONSTRAINT "FK_VENDOR_PAYMENT_VENDOR_ID" FOREIGN KEY ("VENDOR_ID")
	  REFERENCES "UDA"."VENDOR" ("VENDORID") ENABLE;
