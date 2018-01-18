/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/12/5 10:18:04                           */
/*==============================================================*/


drop table if exists APP;
drop table if exists ROLE_USER;
drop table if exists BLOG_TAG;
drop table if exists COMMENTS;
drop table if exists LOGIN_LOG;
drop table if exists ROLE_RESOURCE;
drop table if exists RESOURCE;
drop table if exists TAG;
drop table if exists ROLE;
drop table if exists BLOG;
drop table if exists USER;
drop table if exists FILE_MANAGE;

drop table if exists ACCESS;
drop table if exists SEARCH;
drop table if exists SEND_MAIL;
drop table if exists BLOG_COMMENTS;

--==============================================================
-- Table: ACCESS
--==============================================================
create table ACCESS (
   ID                   VARCHAR(40)                    not null,
   CREATE_DATE          datetime                       null,
   IP                   VARCHAR(30)                    null,
   DOMAIN               VARCHAR(30)                    null,
   PATH                 VARCHAR(60)                    null,
   BLOG_ID              BIGINT                         null,
   HTML_ID              VARCHAR(60)                    null,
   USER_ID              BIGINT                         null,
   constraint PK_ACCESS primary key (ID)
);

--==============================================================
-- Table: APP
--==============================================================
create table APP (
   ID                   bigint(32)                     not null,
   APP_DOMAIN           varchar(45)                    not null,
   CREATE_DATE          datetime                       null default 'CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
   USABLE_STATUS        int                            null default '0',
   APP_INDEX            varchar(45)                    null default 'N/A',
   constraint PK_APP primary key (ID),
   constraint "app_domain_UNIQUE" unique (APP_DOMAIN)
);

--==============================================================
-- Table: BLOG
--==============================================================
create table BLOG (
   ID                   bigint(32)                     not null,
   TITLE                varchar(200)                   not null,
   HTML_FILE_ID         varchar(36)                    null default "",
   MD_FILE_ID           varchar(36)                    null,
   CONTENT              text                           not null,
   READ_NUM             bigint(32)                     not null,
   USABLE_STATUS        int                            not null default '0',
   CREATE_USER_ID       bigint(32)                     not null default '0',
   CREATE_DATE          datetime                       null default NULL,
   constraint PK_BLOG primary key (ID)
);

--==============================================================
-- Table: BLOG_COMMENTS
--==============================================================
create table BLOG_COMMENTS (
   ID                   VARCHAR(40)                    not null,
   CREATE_DATE          datetime                       null,
   IP                   VARCHAR(40)                    null,
   EMAIL                VARCHAR(40)                    null,
   CONTENT              VARCHAR(400)                   null,
   constraint PK_BLOG_COMMENTS primary key (ID)
);

--==============================================================
-- Table: BLOG_TAG
--==============================================================
create table BLOG_TAG (
   ID                   bigint(32)                     not null,
   BLOG_ID              bigint                         not null,
   TAG_ID               int                            not null default '0',
   CREATE_DATE          datetime                       null default NULL,
   constraint PK_BLOG_TAG primary key (ID)
);

--==============================================================
-- Table: COMMENTS
--==============================================================
create table COMMENTS (
   ID                   bigint(32)                     not null,
   CREATE_USER_ID       bigint(32)                     not null default '0',
   BLOG_ID              bigint(32)                     not null,
   PARENT_COMMENT_ID    bigint(32)                     null,
   CONTENT              varchar(2000)                  not null,
   USABLE_STATUS        int                            not null default '0',
   CREATE_DATE          datetime                       null default NULL,
   constraint PK_COMMENTS primary key (ID)
);

comment on table COMMENTS is
'评论';

--==============================================================
-- Table: FILE_MANAGE
--==============================================================
create table FILE_MANAGE (
   ID                   varchar(36)                    not null,
   FILE_PATH            varchar(100)                   null,
   FILE_TYPE            varchar(10)                    null,
   CREATE_DATE          datetime                       null,
   NAME                 varchar(100)                   null,
   constraint PK_FILE_MANAGE primary key (ID)
);

--==============================================================
-- Table: LOGIN_LOG
--==============================================================
create table LOGIN_LOG (
   ID                   bigint(32)                     not null,
   IP                   varchar(45)                    not null,
   LOGIN_TIME           datetime                       null default NULL,
   LOGIN_DT             varchar(45)                    null default "",
   USER_ID              bigint                         not null,
   constraint PK_LOGIN_LOG primary key (ID)
);

comment on table LOGIN_LOG is
'登录日志';

--==============================================================
-- Table: RESOURCE
--==============================================================
create table RESOURCE (
   ID                   bigint(32)                     not null,
   URL                  varchar(200)                   not null,
   TYPE                 varchar(45)                    not null default 'N/A',
   NAME                 varchar(45)                    not null default 'N/A',
   PARENT_ID            bigint(32)                     null default '0',
   DESCRIPTION          varchar(300)                   null default "",
   USABLE_STATUS        int                            not null default '1',
   CREATE_USER_ID       bigint(32)                     null,
   CREATE_DATE          datetime                       null default NULL,
   constraint PK_RESOURCE primary key (ID),
   constraint "url_UNIQUE" unique (URL)
);

--==============================================================
-- Table: ROLE
--==============================================================
create table ROLE (
   ID                   bigint(32)                     not null,
   NAME                 varchar(45)                    not null,
   DESCRIPTION          varchar(1000)                  null default "",
   USABLE_STATUS        int                            not null default '0',
   CREATE_USER_ID       bigint(32)                     not null default '0',
   CREATE_DATE          datetime                       null default NULL,
   constraint PK_ROLE primary key (ID),
   constraint "name_UNIQUE" unique (NAME),
   constraint "role_name" unique (NAME),
   constraint "role_create_user_id" unique (CREATE_USER_ID)
);

--==============================================================
-- Table: ROLE_RESOURCE
--==============================================================
create table ROLE_RESOURCE (
   ID                   bigint(32)                     not null,
   ROLE_ID              bigint(32)                     not null default '0',
   RESOURCE_ID          bigint(32)                     not null default '0',
   constraint PK_ROLE_RESOURCE primary key (ID)
);

--==============================================================
-- Table: ROLE_USER
--==============================================================
create table ROLE_USER (
   ID                   bigint(32)                     not null,
   USER_ID              bigint(32)                     not null default '0',
   ROLE_ID              bigint(32)                     not null default '0',
   constraint PK_ROLE_USER primary key (ID)
);

--==============================================================
-- Table: "SEARCH"
--==============================================================
create table "SEARCH" (
   ID                   VARCHAR(42)                    not null,
   CREATE_DATE          datetime                       null,
   IP                   VARCHAR(32)                    null,
   KEYWORD              VARCHAR(240)                   null,
   constraint PK_SEARCH primary key (ID)
);

--==============================================================
-- Table: SEND_MAIL
--==============================================================
create table SEND_MAIL (
   ID                   VARCHAR(40)                    not null,
   CREATE_DATE          datetime                       null,
   IP                   VARCHAR(40)                    null,
   EMAIL                VARCHAR(40)                    null,
   constraint PK_SEND_MAIL primary key (ID)
);

--==============================================================
-- Table: TAG
--==============================================================
create table TAG (
   ID                   int                            not null,
   NAME                 varchar(45)                    not null,
   USABLE_STATUS        int                            not null default '0',
   CREATE_USER_ID       bigint(32)                     not null default '0',
   CREATE_DATE          datetime                       null default NULL,
   constraint PK_TAG primary key (ID)
);

--==============================================================
-- Table: "USER"
--==============================================================
create table "USER" (
   ID                   bigint(32)                     not null,
   NAME                 varchar(200)                   not null,
   IMG_PATH             varchar(100)                   null default "",
   PASSWORD             varchar(100)                   not null,
   EMAIL                varchar(45)                    not null,
   PHONE                varchar(45)                    null default "",
   ADDRESS              varchar(200)                   null default "",
   LOGIN_SUM            int(11)                        not null default '0',
   LAST_LOGIN_IP        varchar(100)                   not null default 'N/A',
   CREATE_DATE          datetime                       null default NULL,
   USABLE_STATUS        int                            not null default '1',
   MODIFY_DATE          datetime                       null default NULL,
   TOKEN                varchar(50)                    null default "",
   LOGIN_TYPE           int                            not null default '0',
   LAST_LOGIN_DATE      datetime                       null default NULL,
   constraint PK_USER primary key (ID),
   constraint "email_UNIQUE" unique (EMAIL),
   constraint "name_UNIQUE" unique (NAME)
);

comment on column "USER".USABLE_STATUS is
'0：可用
1：不可用';

comment on column "USER".LOGIN_TYPE is
'1：用户名密码登陆
2：QQ登录
3：微信登录';

alter table BLOG add constraint FK_BLOG_USER foreign key (CREATE_USER_ID)
   references "USER" (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table BLOG_TAG add constraint "FK_Reference_8" foreign key (TAG_ID)
   references TAG (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table BLOG_TAG add constraint "FK_Reference_9" foreign key (BLOG_ID)
   references BLOG (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table COMMENTS add constraint FK_BLOG_COMMENT foreign key (BLOG_ID)
   references BLOG (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table COMMENTS add constraint "FK_Reference_3" foreign key (CREATE_USER_ID)
   references "USER" (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table ROLE_RESOURCE add constraint FK_ROLE_RESOURCE_R foreign key (RESOURCE_ID)
   references RESOURCE (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table ROLE_RESOURCE add constraint FK_R_R_R foreign key (ROLE_ID)
   references ROLE (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table ROLE_USER add constraint FK_ROLE_USER foreign key (USER_ID)
   references "USER" (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

alter table ROLE_USER add constraint FK_ROLE_USER_R foreign key (ROLE_ID)
   references ROLE (ID)
   match full
   on update restrict
   on delete restrict
   not deferrable;

