create database if not exists radius;

use radius;

create table file
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    description varchar(255) null,
    title varchar(255) null,
    updated_at datetime(6) null,
    url varchar(255) null
);

create table gateway
(
    id int auto_increment
        primary key,
    name varchar(255) not null
);

create table geolocation
(
    id int not null
        primary key,
    code varchar(255) null,
    name varchar(255) null,
    three_char_code varchar(255) null
);

create table news
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    description varchar(255) null,
    send_mail bit null,
    title varchar(255) null,
    updated_at datetime(6) null
);

create table reseller_level
(
    id int not null
        primary key,
    discount_percent decimal(19,2) null,
    min_score decimal(19,2) null,
    name varchar(255) null
);

create table reseller
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    credit decimal(19,2) null,
    enabled bit null,
    level_set_date datetime(6) null,
    phone varchar(255) null,
    updated_at datetime(6) null,
    level_id int null,
    user_id int null,
    constraint FK39t9mqgxa2np88nh4ath5pib3
        foreign key (level_id) references reseller_level (id)
);

create table reseller_add_credit
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    credit decimal(19,2) null,
    reseller_id int null,
    constraint FKiqtox4qkr3f73mc7rjjwmqube
        foreign key (reseller_id) references reseller (id)
);

create table reseller_level_coefficients
(
    id int not null
        primary key,
    active_subscription_max decimal(19,2) null,
    active_subscription_percent decimal(19,2) null,
    current_credit_max decimal(19,2) null,
    current_credit_percent decimal(19,2) null,
    deposit_interval_manual_days decimal(19,2) null,
    deposit_interval_max decimal(19,2) null,
    membership_duration_max decimal(19,2) null,
    membership_duration_percent decimal(19,2) null,
    month_credit_max decimal(19,2) null,
    month_credit_percent decimal(19,2) null,
    month_sale_max decimal(19,2) null,
    month_sale_percent decimal(19,2) null,
    total_sale_max decimal(19,2) null,
    total_sale_percent decimal(19,2) null
);

create table role
(
    id int not null
        primary key,
    name varchar(255) null
);

create table file_roles
(
    file_id int not null,
    roles_id int not null,
    constraint FK3r4pxwbt10tgv1i44byccxmfa
        foreign key (file_id) references file (id),
    constraint FKorq7cg6v7x93a24crnhgp7jie
        foreign key (roles_id) references role (id)
);

create table news_roles
(
    news_id int not null,
    roles_id int not null,
    constraint FK7rnc4was693yp0n7yq2cii5uo
        foreign key (news_id) references news (id),
    constraint FKrqoe6ok05ewg1cisb2fvbjuus
        foreign key (roles_id) references role (id)
);

create table server
(
    id int auto_increment
        primary key,
    city varchar(255) null,
    country varchar(255) null,
    created_at datetime(6) null,
    description varchar(255) null,
    host_name varchar(255) null,
    kill_command varchar(255) null,
    ports int null,
    private_ip varchar(255) null,
    public_ip varchar(255) null,
    root_command varchar(255) null,
    secret varchar(255) null,
    ssh_key varchar(255) null,
    ssh_username varchar(255) null,
    type varchar(255) null,
    updated_at datetime(6) null,
    ssh_private_key varchar(2000) null,
    constraint UK_82wg05oxphw77cf2dv2pn1omp
        unique (public_ip)
);

create table service_group
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    description varchar(255) not null,
    discount decimal(19,2) null,
    language varchar(255) null,
    name varchar(255) not null,
    deleted bit DEFAULT 0,
    updated_at datetime(6) null
);

create table group_app
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    daily_bandwidth decimal(19,2) null,
    description varchar(255) not null,
    download_upload decimal(19,2) null,
    duration int null,
    ip varchar(255) null,
    multi_login_count int null,
    name varchar(255) not null,
    price decimal(19,2) null,
    tag_name varchar(255) not null,
    updated_at datetime(6) null,
    username_postfix varchar(255) null,
    username_postfix_id varchar(255) null,
    service_group_id int null,
    deleted bit DEFAULT 0,
    registration_group bit DEFAULT 0,
    constraint FKtouoho0ujtluniystqjyoauoc
        foreign key (service_group_id) references service_group (id)
);

create table reseller_service_groups
(
    reseller_id int not null,
    service_groups_id int not null,
    primary key (reseller_id, service_groups_id),
    constraint FK2qkptr3kdgvyeohi24s5nyvsq
        foreign key (service_groups_id) references service_group (id),
    constraint FKjyq21j66v1uhf5l7rnq2qp304
        foreign key (reseller_id) references reseller (id)
);

create table service_group_allowed_geolocations
(
    service_group_id int not null,
    allowed_geolocations_id int not null,
    constraint FK6ur8drku12byhh1lhu0h5umke
        foreign key (service_group_id) references service_group (id),
    constraint FKthqr8o7r3tiaw1q7riiqt9t8l
        foreign key (allowed_geolocations_id) references geolocation (id)
);

create table service_group_dis_allowed_geolocations
(
    service_group_id int not null,
    dis_allowed_geolocations_id int not null,
    constraint FKmqb3ew7bu3tg5b9t39jhfhncj
        foreign key (dis_allowed_geolocations_id) references geolocation (id),
    constraint FKnuf6s75gi7seqw2b2ps97xhun
        foreign key (service_group_id) references service_group (id)
);

create table service_group_gateways
(
    service_group_id int not null,
    gateways_id int not null,
    constraint FK4oyr1o6684bdv9jhmiq739489
        foreign key (service_group_id) references service_group (id),
    constraint FKddwsiexlbjawslgl5cchdoskg
        foreign key (gateways_id) references gateway (id)
);

create table user
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    email varchar(255) not null,
    enabled bit null,
    password varchar(255) null,
    rad_access varchar(255) not null,
    rad_access_clear varchar(255) null,
    updated_at datetime(6) null,
    username varchar(255) not null,
    reseller_id int null,
    role_id int null,
    constraint FKn82ha3ccdebhokx3a8fgdqeyy
        foreign key (role_id) references role (id),
    constraint FKnw0i6ug7ho5getfmboajqvlry
        foreign key (reseller_id) references reseller (id)
);

create table oauth_token
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    exp bigint null,
    iat bigint null,
    social_media varchar(255) null,
    social_token varchar(255) null,
    token varchar(255) null,
    updated_at datetime(6) null,
    user_id int null,
    constraint FK2y78f55u617km0eu82gnqr7ux
        foreign key (user_id) references user (id)
);

create table password_reset
(
    token varchar(255) not null
        primary key,
    created_at datetime(6) null,
    user_id int null,
    constraint FK3rcc5avyc21uriav34cjrqc91
        foreign key (user_id) references user (id)
);

alter table reseller
    add constraint FK149bhi3y1jflv6dw60urqdlv9
        foreign key (user_id) references user (id);

create table ticket
(
    id int auto_increment
        primary key,
    category varchar(255) not null,
    created_at datetime(6) null,
    status varchar(255) not null,
    subject varchar(255) not null,
    text text not null,
    updated_at datetime(6) null,
    creator_id int null,
    constraint FKqwrtm8gdsjpjppg1ir9ylwaon
        foreign key (creator_id) references user (id)
);

create table ticket_reply
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    text text null,
    updated_at datetime(6) null,
    creator_id int null,
    ticket_id int not null,
    constraint FKa0fc1iaijpkvps7lbmwxxr4hb
        foreign key (creator_id) references user (id),
    constraint FKsiaefs29fsoww8skxpo0g7ddh
        foreign key (ticket_id) references ticket (id)
);

create table user_profile
(
    id int auto_increment
        primary key,
    address varchar(255) null,
    city varchar(255) null,
    country varchar(255) null,
    created_at datetime(6) null,
    first_name varchar(255) null,
    last_name varchar(255) null,
    phone varchar(255) null,
    postal_code varchar(255) null,
    updated_at datetime(6) null,
    user_id int null,
    constraint FK6kwj5lk78pnhwor4pgosvb51r
        foreign key (user_id) references user (id)
);

create table radius.user_subscription
(
    id                int auto_increment
        primary key,
    created_at        datetime(6)    null,
    daily_bandwidth   decimal(19, 2) null,
    download_upload   decimal(19, 2) null,
    duration          int            null,
    expires_at        datetime(6)    null,
    multi_login_count int            null,
    price             decimal(19, 2) null,
    updated_at        datetime(6)    null,
    group_id          int            null,
    user_id           int            null,
    renew             bit            null,
    renewed           bit            null,
    payment_id        int            null,
    constraint FKpsiiu2nyr0cbxeluuouw474s9
        foreign key (user_id) references radius.user (id),
    constraint FKt4tua4acgi5d647mxbklr9a46
        foreign key (group_id) references radius.group_app (id)
);



create table stripe_customer
(
    id        int auto_increment
        primary key,
    stripe_id varchar(255) null,
    user_id   int          null,
    constraint FK50iy5vrvy8g7u0nvbku22d6i5
        foreign key (user_id) references radius.user (id)
);

create table radius.payment
(
    id                int auto_increment
        primary key,
    category          varchar(255)   not null,
    created_at        datetime(6)    null,
    expires_at        datetime(6)    null,
    gateway           varchar(255)   not null,
    group_id          int            null,
    more_login_count int            null,
    payment_id        varchar(255)   null,
    price             decimal(19, 2) null,
    renew             bit            null,
    renewed           bit            null,
    status            varchar(255)   not null,
    updated_at        datetime(6)    null,
    user_id           int            null,
    meta_data         longtext       null
);

create table radius.apple_receipt
(
    id             int auto_increment
        primary key,
    created_at     datetime(6)  null,
    payment_status varchar(255) null,
    receipt        text         null,
    updated_at     datetime(6)  null,
    user_id        int          not null
);


CREATE TABLE IF NOT EXISTS radacct (
    radacctid bigint(21) NOT NULL auto_increment,
    acctsessionid varchar(64) NOT NULL default '',
    acctuniqueid varchar(32) NOT NULL default '',
    username varchar(64) NOT NULL default '',
    realm varchar(64) default '',
    nasipaddress varchar(15) NOT NULL default '',
    nasportid varchar(15) default NULL,
    nasporttype varchar(32) default NULL,
    acctstarttime datetime NULL default NULL,
    acctupdatetime datetime NULL default NULL,
    acctstoptime datetime NULL default NULL,
    acctinterval int(12) default NULL,
    acctsessiontime int(12) unsigned default NULL,
    acctauthentic varchar(32) default NULL,
    connectinfo_start varchar(50) default NULL,
    connectinfo_stop varchar(50) default NULL,
    acctinputoctets bigint(20) default NULL,
    acctoutputoctets bigint(20) default NULL,
    calledstationid varchar(50) NOT NULL default '',
    callingstationid varchar(50) NOT NULL default '',
    acctterminatecause varchar(32) NOT NULL default '',
    servicetype varchar(32) default NULL,
    framedprotocol varchar(32) default NULL,
    framedipaddress varchar(15) NOT NULL default '',
    framedipv6address varchar(45) NOT NULL default '',
    framedipv6prefix varchar(45) NOT NULL default '',
    framedinterfaceid varchar(44) NOT NULL default '',
    delegatedipv6prefix varchar(45) NOT NULL default '',
    PRIMARY KEY (radacctid),
    UNIQUE KEY acctuniqueid (acctuniqueid),
    KEY username (username),
    KEY framedipaddress (framedipaddress),
    KEY framedipv6address (framedipv6address),
    KEY framedipv6prefix (framedipv6prefix),
    KEY framedinterfaceid (framedinterfaceid),
    KEY delegatedipv6prefix (delegatedipv6prefix),
    KEY acctsessionid (acctsessionid),
    KEY acctsessiontime (acctsessiontime),
    KEY acctstarttime (acctstarttime),
    KEY acctinterval (acctinterval),
    KEY acctstoptime (acctstoptime),
    KEY nasipaddress (nasipaddress)
    ) ENGINE = INNODB;


#
# Table structure for table 'radcheck'
#

CREATE TABLE IF NOT EXISTS radcheck (
    id int(11) unsigned NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    attribute varchar(64)  NOT NULL default '',
    op char(2) NOT NULL DEFAULT '==',
    value varchar(253) NOT NULL default '',
    PRIMARY KEY  (id),
    KEY username (username(32))
    );

#
# Table structure for table 'radgroupcheck'
#

CREATE TABLE IF NOT EXISTS radgroupcheck (
    id int(11) unsigned NOT NULL auto_increment,
    groupname varchar(64) NOT NULL default '',
    attribute varchar(64)  NOT NULL default '',
    op char(2) NOT NULL DEFAULT '==',
    value varchar(253)  NOT NULL default '',
    PRIMARY KEY  (id),
    KEY groupname (groupname(32))
    );

#
# Table structure for table 'radgroupreply'
#

CREATE TABLE IF NOT EXISTS radgroupreply (
    id int(11) unsigned NOT NULL auto_increment,
    groupname varchar(64) NOT NULL default '',
    attribute varchar(64)  NOT NULL default '',
    op char(2) NOT NULL DEFAULT '=',
    value varchar(253)  NOT NULL default '',
    PRIMARY KEY  (id),
    KEY groupname (groupname(32))
    );

#
# Table structure for table 'radreply'
#

CREATE TABLE IF NOT EXISTS radreply (
    id int(11) unsigned NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    attribute varchar(64) NOT NULL default '',
    op char(2) NOT NULL DEFAULT '=',
    value varchar(253) NOT NULL default '',
    PRIMARY KEY  (id),
    KEY username (username(32))
    );

#
# Table structure for table 'radusergroup'
#

CREATE TABLE IF NOT EXISTS radusergroup (
    id int(11) unsigned NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    groupname varchar(64) NOT NULL default '',
    priority int(11) NOT NULL default '1',
    PRIMARY KEY  (id),
    KEY username (username(32))
    );

#
# Table structure for table 'radpostauth'
#
CREATE TABLE IF NOT EXISTS radpostauth (
    id int(11) NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    pass varchar(64) NOT NULL default '',
    reply varchar(32) NOT NULL default '',
    authdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY  (id),
    KEY username (username(32))
    ) ENGINE = INNODB;

#
# Table structure for table 'nas'
#
CREATE TABLE IF NOT EXISTS nas (
    id int(10) NOT NULL auto_increment,
    nasname varchar(128) NOT NULL,
    shortname varchar(32),
    type varchar(30) DEFAULT 'other',
    ports int(5),
    secret varchar(60) DEFAULT 'secret' NOT NULL,
    server varchar(64),
    community varchar(50),
    description varchar(200) DEFAULT 'RADIUS Client',
    PRIMARY KEY (id),
    KEY nasname (nasname)
    );


INSERT INTO geolocation VALUES
(1,'Afghanistan','AF','AFG')
                             ,(2,'Aland Islands','AX','ALA')
                             ,(3,'Albania','AL','ALB')
                             ,(4,'Algeria','DZ','DZA')
                             ,(5,'American Samoa','AS','ASM')
                             ,(6,'Andorra','AD','AND')
                             ,(7,'Angola','AO','AGO')
                             ,(8,'Anguilla','AI','AIA')
                             ,(9,'Antarctica','AQ','ATA')
                             ,(10,'Antigua and Barbuda','AG','ATG')
                             ,(11,'Argentina','AR','ARG')
                             ,(12,'Armenia','AM','ARM')
                             ,(13,'Aruba','AW','ABW')
                             ,(14,'Australia','AU','AUS')
                             ,(15,'Austria','AT','AUT')
                             ,(16,'Azerbaijan','AZ','AZE')
                             ,(17,'Bahamas','BS','BHS')
                             ,(18,'Bahrain','BH','BHR')
                             ,(19,'Bangladesh','BD','BGD')
                             ,(20,'Barbados','BB','BRB')
                             ,(21,'Belarus','BY','BLR')
                             ,(22,'Belgium','BE','BEL')
                             ,(23,'Belize','BZ','BLZ')
                             ,(24,'Benin','BJ','BEN')
                             ,(25,'Bermuda','BM','BMU')
                             ,(26,'Bhutan','BT','BTN')
                             ,(27,'Bolivia','BO','BOL')
                             ,(28,'Bonaire, Sint Eustatius and Saba','BQ','BES')
                             ,(29,'Bosnia and Herzegovina','BA','BIH')
                             ,(30,'Botswana','BW','BWA')
                             ,(31,'Bouvet Island','BV','BVT')
                             ,(32,'Brazil','BR','BRA')
                             ,(33,'British Indian Ocean Territory','IO','IOT')
                             ,(34,'Brunei','BN','BRN')
                             ,(35,'Bulgaria','BG','BGR')
                             ,(36,'Burkina Faso','BF','BFA')
                             ,(37,'Burundi','BI','BDI')
                             ,(38,'Cambodia','KH','KHM')
                             ,(39,'Cameroon','CM','CMR')
                             ,(40,'Canada','CA','CAN')
                             ,(41,'Cape Verde','CV','CPV')
                             ,(42,'Cayman Islands','KY','CYM')
                             ,(43,'Central African Republic','CF','CAF')
                             ,(44,'Chad','TD','TCD')
                             ,(45,'Chile','CL','CHL')
                             ,(46,'China','CN','CHN')
                             ,(47,'Christmas Island','CX','CXR')
                             ,(48,'Cocos (Keeling) Islands','CC','CCK')
                             ,(49,'Colombia','CO','COL')
                             ,(50,'Comoros','KM','COM')
                             ,(51,'Congo','CG','COG')
                             ,(52,'Cook Islands','CK','COK')
                             ,(53,'Costa Rica','CR','CRI')
                             ,(54,'Ivory Coast','CI','CIV')
                             ,(55,'Croatia','HR','HRV')
                             ,(56,'Cuba','CU','CUB')
                             ,(57,'Curacao','CW','CUW')
                             ,(58,'Cyprus','CY','CYP')
                             ,(59,'Czech Republic','CZ','CZE')
                             ,(60,'Democratic Republic of the Congo','CD','COD')
                             ,(61,'Denmark','DK','DNK')
                             ,(62,'Djibouti','DJ','DJI')
                             ,(63,'Dominica','DM','DMA')
                             ,(64,'Dominican Republic','DO','DOM')
                             ,(65,'Ecuador','EC','ECU')
                             ,(66,'Egypt','EG','EGY')
                             ,(67,'El Salvador','SV','SLV')
                             ,(68,'Equatorial Guinea','GQ','GNQ')
                             ,(69,'Eritrea','ER','ERI')
                             ,(70,'Estonia','EE','EST')
                             ,(71,'Ethiopia','ET','ETH')
                             ,(72,'Falkland Islands (Malvinas)','FK','FLK')
                             ,(73,'Faroe Islands','FO','FRO')
                             ,(74,'Fiji','FJ','FJI')
                             ,(75,'Finland','FI','FIN')
                             ,(76,'France','FR','FRA')
                             ,(77,'French Guiana','GF','GUF')
                             ,(78,'French Polynesia','PF','PYF')
                             ,(79,'French Southern Territories','TF','ATF')
                             ,(80,'Gabon','GA','GAB')
                             ,(81,'Gambia','GM','GMB')
                             ,(82,'Georgia','GE','GEO')
                             ,(83,'Germany','DE','DEU')
                             ,(84,'Ghana','GH','GHA')
                             ,(85,'Gibraltar','GI','GIB')
                             ,(86,'Greece','GR','GRC')
                             ,(87,'Greenland','GL','GRL')
                             ,(88,'Grenada','GD','GRD')
                             ,(89,'Guadaloupe','GP','GLP')
                             ,(90,'Guam','GU','GUM')
                             ,(91,'Guatemala','GT','GTM')
                             ,(92,'Guernsey','GG','GGY')
                             ,(93,'Guinea','GN','GIN')
                             ,(94,'Guinea-Bissau','GW','GNB')
                             ,(95,'Guyana','GY','GUY')
                             ,(96,'Haiti','HT','HTI')
                             ,(97,'Heard Island and McDonald Islands','HM','HMD')
                             ,(98,'Honduras','HN','HND')
                             ,(99,'Hong Kong','HK','HKG')
                             ,(100,'Hungary','HU','HUN')
                             ,(101,'Iceland','IS','ISL')
                             ,(102,'India','IN','IND')
                             ,(103,'Indonesia','ID','IDN')
                             ,(104,'Iran','IR','IRN')
                             ,(105,'Iraq','IQ','IRQ')
                             ,(106,'Ireland','IE','IRL')
                             ,(107,'Isle of Man','IM','IMN')
                             ,(108,'Israel','IL','ISR')
                             ,(109,'Italy','IT','ITA')
                             ,(110,'Jamaica','JM','JAM')
                             ,(111,'Japan','JP','JPN')
                             ,(112,'Jersey','JE','JEY')
                             ,(113,'Jordan','JO','JOR')
                             ,(114,'Kazakhstan','KZ','KAZ')
                             ,(115,'Kenya','KE','KEN')
                             ,(116,'Kiribati','KI','KIR')
                             ,(117,'Kosovo','XK','---')
                             ,(118,'Kuwait','KW','KWT')
                             ,(119,'Kyrgyzstan','KG','KGZ')
                             ,(120,'Laos','LA','LAO')
                             ,(121,'Latvia','LV','LVA')
                             ,(122,'Lebanon','LB','LBN')
                             ,(123,'Lesotho','LS','LSO')
                             ,(124,'Liberia','LR','LBR')
                             ,(125,'Libya','LY','LBY')
                             ,(126,'Liechtenstein','LI','LIE')
                             ,(127,'Lithuania','LT','LTU')
                             ,(128,'Luxembourg','LU','LUX')
                             ,(129,'Macao','MO','MAC')
                             ,(130,'Macedonia','MK','MKD')
                             ,(131,'Madagascar','MG','MDG')
                             ,(132,'Malawi','MW','MWI')
                             ,(133,'Malaysia','MY','MYS')
                             ,(134,'Maldives','MV','MDV')
                             ,(135,'Mali','ML','MLI')
                             ,(136,'Malta','MT','MLT')
                             ,(137,'Marshall Islands','MH','MHL')
                             ,(138,'Martinique','MQ','MTQ')
                             ,(139,'Mauritania','MR','MRT')
                             ,(140,'Mauritius','MU','MUS')
                             ,(141,'Mayotte','YT','MYT')
                             ,(142,'Mexico','MX','MEX')
                             ,(143,'Micronesia','FM','FSM')
                             ,(144,'Moldava','MD','MDA')
                             ,(145,'Monaco','MC','MCO')
                             ,(146,'Mongolia','MN','MNG')
                             ,(147,'Montenegro','ME','MNE')
                             ,(148,'Montserrat','MS','MSR')
                             ,(149,'Morocco','MA','MAR')
                             ,(150,'Mozambique','MZ','MOZ')
                             ,(151,'Myanmar (Burma)','MM','MMR')
                             ,(152,'Namibia','NA','NAM')
                             ,(153,'Nauru','NR','NRU')
                             ,(154,'Nepal','NP','NPL')
                             ,(155,'Netherlands','NL','NLD')
                             ,(156,'New Caledonia','NC','NCL')
                             ,(157,'New Zealand','NZ','NZL')
                             ,(158,'Nicaragua','NI','NIC')
                             ,(159,'Niger','NE','NER')
                             ,(160,'Nigeria','NG','NGA')
                             ,(161,'Niue','NU','NIU')
                             ,(162,'Norfolk Island','NF','NFK')
                             ,(163,'North Korea','KP','PRK')
                             ,(164,'Northern Mariana Islands','MP','MNP')
                             ,(165,'Norway','NO','NOR')
                             ,(166,'Oman','OM','OMN')
                             ,(167,'Pakistan','PK','PAK')
                             ,(168,'Palau','PW','PLW')
                             ,(169,'Palestine','PS','PSE')
                             ,(170,'Panama','PA','PAN')
                             ,(171,'Papua New Guinea','PG','PNG')
                             ,(172,'Paraguay','PY','PRY')
                             ,(173,'Peru','PE','PER')
                             ,(174,'Phillipines','PH','PHL')
                             ,(175,'Pitcairn','PN','PCN')
                             ,(176,'Poland','PL','POL')
                             ,(177,'Portugal','PT','PRT')
                             ,(178,'Puerto Rico','PR','PRI')
                             ,(179,'Qatar','QA','QAT')
                             ,(180,'Reunion','RE','REU')
                             ,(181,'Romania','RO','ROU')
                             ,(182,'Russia','RU','RUS')
                             ,(183,'Rwanda','RW','RWA')
                             ,(184,'Saint Barthelemy','BL','BLM')
                             ,(185,'Saint Helena','SH','SHN')
                             ,(186,'Saint Kitts and Nevis','KN','KNA')
                             ,(187,'Saint Lucia','LC','LCA')
                             ,(188,'Saint Martin','MF','MAF')
                             ,(189,'Saint Pierre and Miquelon','PM','SPM')
                             ,(190,'Saint Vincent and the Grenadines','VC','VCT')
                             ,(191,'Samoa','WS','WSM')
                             ,(192,'San Marino','SM','SMR')
                             ,(193,'Sao Tome and Principe','ST','STP')
                             ,(194,'Saudi Arabia','SA','SAU')
                             ,(195,'Senegal','SN','SEN')
                             ,(196,'Serbia','RS','SRB')
                             ,(197,'Seychelles','SC','SYC')
                             ,(198,'Sierra Leone','SL','SLE')
                             ,(199,'Singapore','SG','SGP')
                             ,(200,'Sint Maarten','SX','SXM')
                             ,(201,'Slovakia','SK','SVK')
                             ,(202,'Slovenia','SI','SVN')
                             ,(203,'Solomon Islands','SB','SLB')
                             ,(204,'Somalia','SO','SOM')
                             ,(205,'South Africa','ZA','ZAF')
                             ,(206,'South Georgia and the South Sandwich Islands','GS','SGS')
                             ,(207,'South Korea','KR','KOR')
                             ,(208,'South Sudan','SS','SSD')
                             ,(209,'Spain','ES','ESP')
                             ,(210,'Sri Lanka','LK','LKA')
                             ,(211,'Sudan','SD','SDN')
                             ,(212,'Suriname','SR','SUR')
                             ,(213,'Svalbard and Jan Mayen','SJ','SJM')
                             ,(214,'Swaziland','SZ','SWZ')
                             ,(215,'Sweden','SE','SWE')
                             ,(216,'Switzerland','CH','CHE')
                             ,(217,'Syria','SY','SYR')
                             ,(218,'Taiwan','TW','TWN')
                             ,(219,'Tajikistan','TJ','TJK')
                             ,(220,'Tanzania','TZ','TZA')
                             ,(221,'Thailand','TH','THA')
                             ,(222,'Timor-Leste (East Timor)','TL','TLS')
                             ,(223,'Togo','TG','TGO')
                             ,(224,'Tokelau','TK','TKL')
                             ,(225,'Tonga','TO','TON')
                             ,(226,'Trinidad and Tobago','TT','TTO')
                             ,(227,'Tunisia','TN','TUN')
                             ,(228,'Turkey','TR','TUR')
                             ,(229,'Turkmenistan','TM','TKM')
                             ,(230,'Turks and Caicos Islands','TC','TCA')
                             ,(231,'Tuvalu','TV','TUV')
                             ,(232,'Uganda','UG','UGA')
                             ,(233,'Ukraine','UA','UKR')
                             ,(234,'United Arab Emirates','AE','ARE')
                             ,(235,'United Kingdom','GB','GBR')
                             ,(236,'United States','US','USA')
                             ,(237,'United States Minor Outlying Islands','UM','UMI')
                             ,(238,'Uruguay','UY','URY')
                             ,(239,'Uzbekistan','UZ','UZB')
                             ,(240,'Vanuatu','VU','VUT')
                             ,(241,'Vatican City','VA','VAT')
                             ,(242,'Venezuela','VE','VEN')
                             ,(243,'Vietnam','VN','VNM')
                             ,(244,'Virgin Islands, British','VG','VGB')
                             ,(245,'Virgin Islands, US','VI','VIR')
                             ,(246,'Wallis and Futuna','WF','WLF')
                             ,(247,'Western Sahara','EH','ESH')
                             ,(248,'Yemen','YE','YEM')
                             ,(249,'Zambia','ZM','ZMB')
                             ,(250,'Zimbabwe','ZW','ZWE');


INSERT INTO role VALUES (1, 'ADMIN'), (2, 'RESELLER'), (3, 'USER');

INSERT into gateway VALUES (1, 'STRIPE'), (2, 'PAYPAL'), (3, 'COINBASE'), (4, 'PARSPAL');

insert into reseller_level_coefficients(id, month_credit_percent, month_credit_max, current_credit_percent, current_credit_max,
                                        active_subscription_percent, active_subscription_max, membership_duration_percent, membership_duration_max,
                                        deposit_interval_manual_days, deposit_interval_max, total_sale_percent,total_sale_max, month_sale_percent, month_sale_max)
VALUES (1, '100', '30', '100', '20' , '100', '15', '100', '5', '30', '15', '100', '10', '100', '5');


insert into reseller_level(id, name, discount_percent, min_score) VALUES
(1, 'STARTER', '0','0'),
(2, 'BRONZE', '5','5'),
(3, 'SILVER','15', '15'),
(4, 'GOLD','30','30'),
(5,'DIAMOND','50','50'),
(6,'OWNER','100','100');



INSERT into user (id, username, email, password, rad_access, role_id, enabled) VALUES (1, 'nima@orb.group', 'nima@orb.group', '$2a$12$4.IiwafezzxBkzQ6ojigQufkMAVeSK588xrF0e.FD.Ol5EUnZzegi', 'rad',1, true);
INSERT into reseller(id, user_id, credit, level_id, enabled, created_at, updated_at) VALUES (1, 1, '100.00', 6, true, NOW(), NOW());
UPDATE user set reseller_id = 1 where id = 1;

INSERT into user (id, username, email, password, rad_access, role_id, enabled) VALUES (2, 'hosein@email.com', 'hosein@email.com', '$2a$12$4.IiwafezzxBkzQ6ojigQufkMAVeSK588xrF0e.FD.Ol5EUnZzegi', 'rad',2, true);
INSERT into reseller(id, user_id, credit, level_id, enabled, created_at, updated_at) VALUES (2, 2, '0', 1, true, NOW(), NOW());
UPDATE user set reseller_id = 2 where id = 2;

INSERT into user (id, username, email, password, rad_access, role_id, enabled) VALUES (3, 'alireza@email.com', 'alireza@email.com', '$2a$12$4.IiwafezzxBkzQ6ojigQufkMAVeSK588xrF0e.FD.Ol5EUnZzegi', 'rad',2, true);
INSERT into reseller(id, user_id, credit, level_id, enabled, created_at, updated_at) VALUES (3, 3, '0', 1, true, NOW(), NOW());
UPDATE user set reseller_id = 3 where id = 3;

INSERT into service_group(id, name, description, discount, created_at, updated_at) VALUES (1, 'Worldwide', 'Worldwide', '0', NOW(), NOW());
INSERT into service_group(id, name, description, discount, created_at, updated_at) VALUES (2, 'Iran', 'Iran', '70', NOW(), NOW());

insert into service_group_allowed_geolocations(service_group_id, allowed_geolocations_id) VALUES (2, 104);

insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (1, 1);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (1, 2);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (2, 1);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (2, 2);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (3, 1);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (3, 2);

insert into group_app(id, service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, 1, 'Trial', 'Trial', 'tag-name', '0',30, 2);
insert into group_app(id, service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 2, 'Trial', 'Trial', 'tag-name', '0',30, 2);

insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '3 Month', '3 Month', 'tag-name', '16',90, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '6 Month', '6 Month', 'tag-name', '28',90, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '1 Year', '1 Year', 'tag-name', '49.08', 365, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '2 Year', '2 Year', 'tag-name', '84', 730, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '3 Year', '3 Year', 'tag-name', '119', 1095, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, 'Lifetime', 'Lifetime', 'tag-name', '799', 1095, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group) VALUES (1, 'Basic Monthly', 'Basic Monthly', 'tag-name', '3.99', 30, 1, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Monthly', 'Premium Monthly', 'tag-name', '6.99', 30, 5, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Family Monthly', 'Premium Family Monthly', 'tag-name', '11.99', 30, 25, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Basic Yearly', 'Basic Yearly', 'tag-name', '21', 365, 1, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Yearly', 'Premium Yearly', 'tag-name', '49.08', 365, 5, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Family Yearly', 'Premium Family Yearly', 'tag-name', '79.08', 365, 25, 1);

insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '3 Month', '3 Month', 'tag-name', '16',90, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '6 Month', '6 Month', 'tag-name', '28',90, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '1 Year', '1 Year', 'tag-name', '49.08', 365, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '2 Year', '2 Year', 'tag-name', '84', 730, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '3 Year', '3 Year', 'tag-name', '119', 1095, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Lifetime', 'Lifetime', 'tag-name', '799', 1095, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Basic Monthly', 'Basic Monthly', 'tag-name', '3.99', 30, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Monthly', 'Premium Monthly', 'tag-name', '6.99', 30, 5);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Family Monthly', 'Premium Family Monthly', 'tag-name', '11.99', 30, 25);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Basic Yearly', 'Basic Yearly', 'tag-name', '21', 365, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Yearly', 'Premium Yearly', 'tag-name', '49.08', 365, 5);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Family Yearly', 'Premium Family Yearly', 'tag-name', '79.08', 365, 25);

INSERT INTO radius.server (id, city, country, created_at, description, host_name, kill_command, ports, private_ip, public_ip, root_command, secret, ssh_key, ssh_username, type, updated_at, ssh_private_key) VALUES

(1,'Sao Paulo','BR','2021-06-10 00:15:12.119251','Radius Client','br-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.12.149','18.230.148.147','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-10 00:15:12.119251','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAzdjSMIh4J+a8/Rclf/oqF/oLLiVtUtXyCompl2Oj7uyEKpgJ\neE2eyNmP3Z9JcsKY3pTD0ILXr/PPwMQYuWNYQcWPp0eHVJgsAl/Kk40c6EcJXIE1\ns5GonDWLlY6fkvmWqn4YwUg3sidrvr/ThcFy/6nsr7m1P23M8mcrGnLdGzU6Ih40\nnP0DOE36B658vAv4mOG2jNl2ud4fjjxjyrBhaxN60Ai+0BypwXdsRMUEvnCLYQS7\nCGDrSfZ10IrGS/awexRpC/Re8M/nTOTMQct/1M/QUw5N+OwVSk3mRP7Im0tbvrE2\nWxFTQYqO0mLrlQigrhBZKiVrkFJwV5jcLmpwTQIDAQABAoIBABjcRc5cvP48rLh4\nUiwZn3nT1gPVu1VUx22kYJoLC6JwaCnowMQJw9KIDAr9ENbOT74aA2gsVTZH4OBs\nDnxdVixjWs0eI2cBFdeXkBSv5zzaT3Qfmse8ILv07425cuZCRIMExaKaQTe/8RBR\nhI9T5Lqh4Bb8+7nUXfssoJUKKE//ry1Ewsmyb4lkfF1+ApphJLv5UWtzjcnSO5fP\n/e+Q+3m5B2btpGg2ZiXYo/eGcl/+8imRZn+oeWmZBE8qKCprg2ksKcaDO7dNv5ij\nPHEyc63MpgFos8g3ylz2Jpfhi+xjI87JuCE1VjBqcaVjA7PhDnTnCJ7Slewm4oqr\nOW641nUCgYEA72Oa7KhVgwyMqgpyMvTq6VGFu5msg06GffcTIqzHh6tyYtvdKiBo\nyav7sEcisZZ3Bp55BbuE6RI/bDCblGx1zbEh5fAx5+cfXFoy+PY7tFHkWegSba1b\nc5WFneO+VOASgi2LAO7qTzB09FdGJUoFCf/tyseWaPJcmetwEQ/WcOMCgYEA3CFj\nrfPnNck8Ur03HDFrJ8JUAtEeWyplRE9W3fcqHmQtbbsEf7Mzt9cp+cMDld3luBrj\n8IUCRNfd6uy8RnUSJY5qmxXnt6zpusWUphjZrAAk0G2tiJ02069ljU2dWXAmgTTb\nE7kEp3FJSV5WaHewOme0/cf7ThVQigJGF+/2UQ8CgYAuf9CGC6Sgkf14whKwpzZC\nH920NQN3dfdV3HpRD/Kp/bl4TnDFUApKsidvdEHe8PUpyfqEDIqiozLClyaBTBgN\n3kxRV7i57QEAdVzVUy8Bta5/cD60IypSZ0bPBn/gCK5Prv0DL2VAAI+XLqSndZtI\n9/d+ngovVHR8SWk5RzRGNQKBgQCdyi1KZrfIPSxJXqpfQik+QbS4pC9vR895DKoG\nx4HgcQyDSHgsFUcwM0I6bwhl574fnt7e7Gi48kd3eyRmGMreMtzFuAj2BzMvFyu7\nd7qn6R56bPPkJlVDnclcZVMx5gsjuMCEva3RXIoc1kt37Ltc0jPRagUAbpThqEmY\nJQDaDwKBgQDfRtFSoNdVXproCBrFZfNBCbfUtG9RqAxY0eiUQjUS4784xaAjFwM8\nyeQr4u7xx/i/8oT3i/yrEWqqcGYMPb1YfDG9FM/95VsqLZTVLy+MNourArMmpUgf\nEueNapCH50hPeVc+aAgL1CLhSAC6CRtXOGkoi7argqdwPj7JiYTUog==\n-----END RSA PRIVATE KEY-----'),

(2,'Virginia','US','2021-06-13 22:08:32.635036','Radius Client','us-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.63.182','100.25.181.80','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:08:32.635036','-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEArbALib6ahprY292lhicOAHlJiu93rXTeR6FHKKoDf0qq54Si\nY1c1OfbUj0lNUy3cfOTjD60VIvx8c3OnuPuSmpUYrBGAIhDDsQPUe6kpifR1LpqV\n+zfw3Ypgoxe+ia4/DmcaVCJFJAG9Sp5d+RoHX84HLqREn9kasYU5srYT/iWr2mnW\nN0QIfowTlwgMfWi8pC0IsFFFhPzJIESVMIxvEOkwrwvTgnNDL0L9CHlAAR1EhGlJ\nai+sIYGHPlo0t5++r6TyinBTRIhMOsrUx5YIpZlpVM+h/rlXkN0v2UnBqr+PYGQO\nUAJCHCM+x660fdwLi0temZsrm2eva79xiT7xYwIDAQABAoIBAQCUGjhMdV34h1OB\nb6d0FBtJZiLCuN1Y5/2eILS3xPkEctv2XM/HW5aEXaf0onGwCrwnN1kyhckvMxK/\ns5ACGWpyK86E8stKwPbNG1H1PFKyUuQVIDXhqFrX3eX3TAjqB822702xDiPcxyrs\n1MuW3I4LMHhJWkM19n9ffhWsALxdicPBffBRO55CKESQ3I7AFfCRYXreABlxbA6w\n0ul1QrhAimtAYBqNgo36A9VdF1kQrgSY4fRFeAG9D/vev6x2aUuZhTiAtdkg0swE\nBqgnqKE6YeMzdatxhqoWHAhVSI9dx4gu6hs+TNwbJ8lvh+g49HRQebM0BwYHRzlb\nIFCSFkSZAoGBAN35s3U+I0IhjiPksn4phWdguEqN8xHgMP47jBz2s00lKVa9uGhv\n/nTGcIVZ3W3JIcxoFjq4rPI9skZjpLRHN0rLoovIKvNMvZb5OG9MuqU+XnnDS5O9\nEQipCepqKYpG1i4DIesSCwpzzEtoJRAehTb7wTrwhJmSSu/8l0s+yxydAoGBAMhP\niZBSUzDrF5YrZ6XquTLYUL1P4ot2tE9RIuc9ujs3SqbpSgXgACrPaPAYD13z6bBl\n2NJmozRYIbBQ3BF0b5JPMR0en1GnYJcEjMP5rtWBuLtieWHlPWhlMWUekcfsgPc9\nIpLoGVW30JP9oYdOfeL9vXUGXrm7sArXJtA1GeX/AoGACe5F7RTelvLRXSG6KpLI\n+RsGo68+FKqnBni7iAAxfV4QP2U1ftaj162ZEx42HddFv9NYS0A/3IaOF0AV+krF\nWn9Jwl8uLLxeLwbvU2tnXkUwDNqD4xQuUujdHLOrMBiDNIPHXLDt9YXAug7quZHj\n+9W/i1d/Vy+IUrxGOIOSqrECgYBG7MqznEKyjntCg8nmWI8MrsUf0vpPEU/UydDG\n+fdH4lL/eJjqLZ9BiUEJyisexC+ZcuZr0UW2UgJTsL3LPGQI9RtZyS72NYKg1k6V\nUYITPSRQzRRXkljE7xenS0So+lfLswzb4WyfCj/DNsoL48H1Loz3s7K0pv01SWsH\ny9WItwKBgEL4mwa8+szUQxIm8D19mCDwnFV7VI32P6BekULmDY4RNdm4P7TlMCXS\n3VTI5qqBuPr+1EyS6VGnm87sHVUZJ8R6Zx8RsnjbgtO8G6/nPr3WNQc2SoD0n501\niZGXtsd682Gm+9UNkBTfP7DhV8/SBzUHLhNXB8ksCSXx1ldIGK2F\n-----END RSA PRIVATE KEY-----'),

(3,'Ohio','US','2021-06-13 22:09:35.289589','Radius Client','usoh-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.17.55','3.131.119.158','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:09:35.289589','-----BEGIN RSA PRIVATE KEY-----\nMIIEogIBAAKCAQEAraQotk9ZRJp2v5KJ6qkQwQ2pSS0GP3Ul4hf9HHhn0p27UCXj\nrtwVjQHWStonOhh3mg7eImG5Hdc8delFtkrOyt+oeH2/yByF8aYdSm9GhpOw1Dub\noCuMqUYFq+ZTw69nav782VwXLU0JVnWLFRga0Nq2MfLgyJdCOubNsJV0kXedaW1k\n8g+AGrEYTHUOf6dGqY1bGF0MQOfbytKdtJVojxLWE95Mq+R88GvnI/X/XHHFOO49\n0f3ktK6Ae7MiOJglS2ZOUqcZhusjYBVCN7+Rp//VWPGAV98a2yQBFoHo4/ZH+8bS\n350ihHdppGddiu75vP7pupVKZ0tSevS9E94PxwIDAQABAoIBAE6AoftSQlx2rOsN\nWBbkGnP9NiSkG2gDEX53M/J0KRQaRX4FszrxuziFtRkgKY3lU+UthZXrsmJgVbBF\nJ0Qz6+viU+sths/0vUWzwRhO2IatLMu2qzQtnClHQ/ckaYhVu56u6ydlsADGDEuv\nVpLR1AXKa4aHdMKbR9ETWbjUyN+eglYmMx0CDWq/eoQ8lgNImafJrahSFcWe/a66\najlLqZXVUI+gUQSuCNW4s3jqWld36o3cupvNb5TeWZfgobzHdWZ/0ptQ8hiqZhqA\nH9ESZdA2Kb3i6X+o2yNYHPn7DL03BXyeCUzniDWpqbMAus1z9NjIFl23L2afrhWa\nWwhLDAkCgYEA4WqLWUrE2Zs8LVjnGlShf0Dzk9eGpymV99otYoPqSTu8l7whing6\nQ5ztIhNQZYiMhlNHwl69HFZtt/3RrJncLg+z45TECLLmXycVOASlafmDEiZ2/P6Y\n4v5PKYhY2e6/mLKXHHf5EsPUAac+E/QtiuP6tJ28eLVu1Zmd0lWBpGUCgYEAxTNM\nYMQhkAj991FlCB6O4cQ1rY6wEPSdQ8VOML8jLcJNusMKdEaqC/M0UM5X93RMW4Gy\nv0nQeWlbNaHvKoZQvVGC8DAsr2ej+PaUwrWD5zh2cDL7kXQFb5TZDj8ykKdDkd6C\nmTgEgst7mzyaOZgmFHTVhfXo1kaVhRYBm2C+crsCgYANGCJWIojC10isnbB3EBl9\nielcWjXBOiIzDqJ6SY1viZ/P9KbCI2/HA7OmuhZ+f2siVm4htV0o60BeYskuTJXE\nuzbI7phgpPvK5TtPUL3HJS5e/IsG8UcyUHGKyRfYOQ06Mo2iorl+RI/pNKzffdyz\nZ8DlgxAytt9kWxMW586rHQKBgD9Tx/VTRsvwd05S7zT6SzwpPGut5NCtHUvEMuXo\nm614NmoBdHv+rWEFCwngGdO3n0XLktFnLPGwacABbsKjO45wfLIRZmg1yiyLUx5s\nGB3Kvg4t8Se2sIAytrmiheR6MnLHl/0IRjqSifFCNuzD9YagNmm8nLcjF6UTehSh\nrsc9AoGACPiBwyl2w7rMlxgKSZsSSU1bWewQFJCtOID0DgAM1hlfSzERV26iIrOF\nt/iDcHO8SujhPy/JJDEUrUzEDN0hvbJRg4WkSzfOwE1YJJihLRsaWJ6toXVvM9QA\nKeaPdvLQ4QS19gVuVwbpF+XiybOSOukhOapbNj6V0g9IBwSTP7E=\n-----END RSA PRIVATE KEY-----'),

(4,'Califronia','US','2021-06-13 22:19:30.642299','Radius Client','usca-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.9.102','18.144.81.121','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:19:30.642299','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAt/wNXAYkRJijrUC8yvTUFS9iBwU89hhVal9FhaIqlByFQB2Y\nZsPODlINZYz/4qSkRut0SQIpJNzDD72aJdjEl9jRw1VCl+IEGNsErJYN3Cc+sE9N\nTKtcvyXH4LuBT76OPDEPN3ls5Nz/ipazNwE2prIhyEcQrLSMnAb43KlRB+ksLOi7\nvnn+v440306A1Jnr+b/CJbBKruBj07NuDhLd1fFVM8AhjfHMhyPGWvhYpdCRKkdf\nbNWASQVyvYz4HjhWXeuPZMNjOXgAQzsEUOBMTzJ2FB3mJKbYPUWDfmuDoAs2Hx/v\nz04COvA1VnwIfD2XYOf1BZfJmNNYAkflu6fCTQIDAQABAoIBAGe9VmcSC3cmPfwd\nqZYisEb1vadJu9Lja54b9B5ijvGOl0/MoDcpptEKUQV2mH3Z6csXTi0wa7SeSdph\nRT6b2FXqanRiabcsOLUvIZaS0WPWORG+1U4qvZQGIIrGDybAwoCMBRF5hP4S4n/p\nc0nLhF4/yK6d63gGIdaEU+RQECGN0jgqFTsuqBPTliODRvPmgH3ZSWcZDruTd2kt\nxlrt96pVQI+B4TnfFTxFBvjQD+kac47KblovsYmmqFFA58dq2axL+OQDjEHqZzUy\nRKQgCZElRjRT8php/HdnR416lZfPjycWcXDe8q17S64UKviRVn3OaOHInhHo57j7\nRmptKoECgYEA5bqYOC62dA5qwA0tDkjPsWGX94h8zmFUfdwah39/QNQstdk/eZ6n\nYMnxh1L4Tm2N1p0hMRAJKTp4+U9JPflRGpnvkTV36tk83xPBIV0h0st7OaXkW50O\nwVtWzXMyKqz7mzej/sOASb0k9vkWkhgtndWqV7FVDW4A1Fr5miOseOECgYEAzQZG\nToAWUbxe47SIVa8XKJKrk2I1Wax9TwH5RLavlVQzmSz4dXmWLLqf0Ddw4WoTTAZd\nD76fy/d1ENfzybcmZJGdiDvGiEdbG2yjPT0FUA7Kc3znT0R3p5YG9yGl6UdgjkVA\n7XgfaluLIF9Ek4RPDR7iibqyiRs4pS86Ss5VGu0CgYEA1mEIx/UforIJgGtLTnc4\nCkoOrJqcDWPamxEZWLTsJa1ag+DWUUJy4+nQEP6mZVnq8Qn0/Xn/co/0cINC0Cin\nPPHGFHYXD/vlyC8BHYdxlur4dVWQAsZQDLSWDmefux9uNOz5hzcdsrJlmmvcWj1h\n5FaN/gOoYT30XDcBkowT2UECgYEAhwIewgqup9w3UBLNxD6sLVeIDGGbaBWh3qka\nEBmMvfTYlYF7i0ApmAK8wkv/1TAYzPNTzM5mQ9YMDK9oJeipK4iIB/H05kC2yfRb\n+jN4kCNtRWd2mkgn0v+ETrClpFwQRR9thfKYHV1y3Dpf2hRCSg2Qd8mtT4PRZVUY\nObJtcHECgYB0yji7KTR/FWUyigxSepvz3sm+MtHgesQSssi+R63U3NLFaHWEZrVj\nNKZs1twjZ1RT5akPZqIXbbDi2Y4jK4rosOVvxZtGxyR5csrqNKsmvBogHy4G+Mht\nvm+ry158TARnLl13r1SRpS2IvAPq2HQz97N5wK4hqskfm4aXOFhfpA==\n-----END RSA PRIVATE KEY-----'),

(5,'Oregan','US','2021-06-13 22:20:38.133710','Radius Client','usor-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.2.174','34.209.25.245','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:20:38.133710','-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEAiyNXx89x4a8PidN18NeibLn5mGEQgYQmW6szid4oc9lxF/nh\nM6IJ92tW5wBNLe/M2U9RIS4lJKm58M/iFpyKpTviiYFGlhzk8fXfPuPAKJi0krSO\nRsnJkC5ESOjCh3yBkxUuNNCbHbM+wU46l2i+zAi6wWdMTbgJ4mrCZAjhNooG4ZRD\nd9B5dCit5fdbohDJqBIxYJ3g7kz2ur9Lw3K1tXYFVHrtzCM0YpM50KLbRnpUgy9+\n4Ym/9v5DfSdDq6pGfCiiBFf96SoYqrdRt2BIyhcvk65TZooTMqn/EglBG+2+jP6I\nMg7VML4xrHUoo0ibrQKwHrsqb/FjaOWDwAO46wIDAQABAoIBAARfEOPzLYXEpT1U\nSnFNpQm7cg8KhaU8DKR2pYDgXO7+1uHE9QFVRvDiC3HofYRC83GkX00mkP4s4u2g\n6l6Bfo2gawac7CXNNJgxfFkSeLgNzCaLy6iHsHB6M6UCwxHUpJGFadU49JajSgZy\nub6GCPCKGav33drXPGBDsGXKLaEz+wlE1s/RPh6nMnYQRgKHCPdgZfENp2WiPG44\nv7ZD/OT++ZoghDC6fWyADDbGOlVrM9urgG1YGZcnEXjFfKtbBAg3tqKKJ/wokRzn\nH/yt7tZmoTg8QEJ2XwoBhW7r5SXUy1Us2QFuKDtFBk6w+sHjwZBwXD5DUOUGc5Wl\n6hLZIGECgYEAzmmoA2slGKxlu0cqpw13QgX/caULc3awk6OBrr+E9BUMHsHPYrj+\n8POL8ddb1ufP22NvmB9+4Oir89ZRGK2wtY/if2a2QhiZ+SzFqXITLQoBNGOTRQ8R\na5mfV5jntj2XqgqRpmafbjZE+svAkw7nA257V3C69SlRoMc3pRsY9fsCgYEArJBO\nthVdraFthG34Vjyw95rw5BscAeJVYqkvMvVLe6EtByKhzk7cYWYkaRF3nhacY6Fi\nJJH8FXtnPuMEvNp0e4JAD6+Pw7ubR3fySuvqkrHamPt0Y6tRLdRnGk0Tm8/L7Tlr\nfevdmkZIxwObadS4sfTu9GP0INfNENL6HrRvBdECgYAkbLaua8DvGrUVGFjpg9HQ\nv9g1CMf9hytf0lG3MarXm8M4xkC/jh/HC2NduQBG6z9d2q09rgkozUZS0DYdYcE2\nOP7iNpDhFxBMzV7QKQZDQ2m1AgFiBWewkjpxLdVt7m6OHjFXqPJnc4I2s6/3A4E2\ntM9lPcpwKutxsNYb960u8wKBgQCfKftvtwVQwTRikYwc/p27xA2sV9TdaAWZmrMO\nLXIR9feyPR9BSzu6Gpw6zQGnrvNQceo0EPKSXeA9AOe9yUDnKpwr26KexsvSf6/b\nqv4RkDowIL6Zr8qQmvj+KVmMvX06sVuUKZmcUneOGKWgV7SDb4d3oznpkSJ0AiTx\nbTt6AQKBgE/zGoH+nSe6+BXb3QA0eZJ1D1qFuKbAyViEmtKYdlr1TemEVWwP2qoa\nPXj0fxTMs2QUvIZ9y1YC607GQCZ/MZmVPyZFuoNwxgS6ghVEpQ9ng0lUubGzo9Sw\nXhkqlT8UZ+5z7YsC7o+7OjVLTj73FRCUhWnJawIrqhEzSW+X3uJm\n-----END RSA PRIVATE KEY-----'),

(6,'Cape Town','ZA','2021-06-13 22:22:42.407702','Radius Client','za-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.22.229','13.245.16.178','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:22:42.407702','-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEApRUZdznBc+Y7863Zl7LAQ40aRcZHgjJFDbGlYEMFtkk6lIcL\nEpegqxw81teO3NX67WCZ1X9t1K6gyWrdmHiYe3PtWTKIvujGJold0c4oruB98eRj\nYIbGF0zAHqB9dcRsF67PjD7RvNGUawDNfTQVpFy2Cm7XUHrzwv268UMKAdBOFQcr\nWDOfgXKfViUXKLKeByM4pdbS8enTKoRisa6rVwcpJx7BX8orIT+QKcJ7ILkaZEXZ\nWg6eKJ57LUFlkJu8G5cU4HXka8FJiQ9439i40rQJGNtV8aoD74H+D0C1Dj/IsKvY\n+wgDU8wVb8WogG9+h/LQe1rdRvjntg6iz7+SNwIDAQABAoIBAH6Su/DPRsWfjO4c\nqQ6IDqR8pt1C1wc7lfiW8a7qFJIceZ7yIApEWft4biloI3CtXHTvMWQiDUsaDcwB\ndSB3rqpRS3Wtqm1+OpfHkTjRch4LoGV8cLnM/EtXHKMe+LL4RMyvyxDEBbpZkwus\nFOJfsgxQzX8YV1ysuQQT2WcsuJqEKaArCAOXT/Z4vYmQwhezf2dnqNlQNoDeBVke\nhn1P73Rf4uxPRCQpSLqAV5jZV+289/asDGdKaE25TvKbOKBZD29tlB4j1XzlgRtx\nJyx1xrHURYiZQKFJpOagd6EpsXS0TJBss3v9cyKPIe7SexxtvmOo5iWgf/EOVP/+\n1HxOTyECgYEA3LJiVZUEViATFHCkvfuDBazNJs0K2fy7Ujy+mt96Jy7tJ6Z6UB3h\nqLj+zpJSOL2IcyCy8izg3Ywgxmq1mAVVbX/TSCeem4U3eX/+IYGucvQUxTCKKK0D\nEG1QXregEUDaWKbLI9aQGG/DSjI6uqyyWFQAhFxT0AqONNMgQifpW2cCgYEAv31J\nEyERHleP/hBpcyGc7jALnZKBgAlMNEeGegg0vyafVfxZYtPJIdRXGcs8ZFz1uGDv\nIsRBfGdbJVOB/+lnk3rx9uWVDSg+aYBOYxew/pe5tvhoMsM02IlGA9II1QEVHpT8\nOWFVlEBfhblzv7DLqN98et3o6LMYQyY+BPohoLECgYAS4pDXkfDVDdmUHi96slL7\n4VWMxG7xSFcfKmOFDSp+v907T1uYD3aH4YysUIkIoe9TKrElWAHF83+6etcfMxWk\nURPbUMiZtkSQ+QreAyLSY2CDnyuzX1qrxS7jbAMedtY4T82CO5IJBUdIyhOMdcqt\nFEMXEG97leulSYuxHYkycQKBgFBRm7ITX5vLfJFDjweEpM9fha+9QOZUgAs5qRVM\nldtLtu+ZHmEOlkHDUBl4nsc2GrcQqZ16lkE5FQkOAuw1mYVJv24hFCjlpXAZKkf0\nKwBO4c32WbZZHLRqt9xoX94ASzx9n2HuCrhZW/e6NuvDm1klDJB4vfXFETsxMjhi\nNnKxAoGBAIVNZ5jzykDLTgnu0kh39KRH82oUYGUv1oMI2U0ZtwMccsBXlTmJAIak\nD6f5nd/Z5DhNpbTFsfJp9CkannRzLbp1sonPHUL5bRgf0Hdo67dFnw/jsWj2dSrJ\nu3+t6k1jKSBNQWw7E1sHV3k6MYUOOXx5Sn7wgX4GBLjnuVmXWlKw\n-----END RSA PRIVATE KEY-----'),

(7,'Hong Kong','HK','2021-06-13 22:23:51.032822','Radius Client','hk-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.43.55','18.162.225.96','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:23:51.032822','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAt29/vchVsuZD9tQMbdMhPPVbaSacc2GXjki0dxgbRcRgbC16\nrjy5anFJ7XWZ+IPZXhuLYGXGTiqJHdHUFlgnSelSln7F1mIXSu38P9QyFZLx3uXZ\nx9o6ayBS/Tu0wKGpw0cVg52+plKVxIoFO1L6egmRTuZsxmOMH0qB109JJZgtteEO\nQ9S6qrWIhSJ/umA/U1xBs8CihXoCCKi3M92KZ650ZBpjofCRZPFaHNB/3swPJyOk\n9n3M6hatvfqHPxv31sQ7Z9GXHiOIyaq8uNBxEjtzPwF+y4J+U0vbv6nVTcYo3aZE\nFa2fHNrCKla7mFX7C+ioGMmNZ/oSVJ6dABw7OQIDAQABAoIBAQC11aQ2N9Gj7phu\npBLSB1lKeRmtKWYrBhHbLH0gFVhrl1fRxF95Op6+QiD7b/k6HVEq9Xj7DMhAEfAs\nDCUGJUqE/PIuVuq8Mi5Oy4yipTEkq65xuz1f3BbOvJMnjbkGvvpuphdQid1EOFdY\nJU08WmwK71MIeGhZXLK5Df2ZtnGW/g4i+SosrUd6PjQBI3xlu+If0L5bFq2pbR17\nCFf9xj3qJMzkubtTDRGcfkVoSfmETEd4JVPxo1z79IWfu29G1zC9arUAOassF8ze\naUp0w+wEYYoc5dHOzszP8hZl8l2mugjA+r8T2OFpNP+B8pvYeor6+YfMdAJUSbM9\nWhKjh++RAoGBAOOvbhTUc2FHBoV2pNsX6jVPb425Qr78PCTpiQ/TAz1lzNSKbM+0\nx7AU81mlL2m7JoSrKwhhtoR/uXyIf9gv2pTpdvdLx9iYe5S/n7Peh4KRvFSJJHFY\nI7vsiJ9syDZSkLWBwfvzaP5F58RiDutteDlJlOUH1+s8IoH7sqp1mnHdAoGBAM4/\nVo/qLcs+G4TUmwqr1KgzDPK6n2WGEgNoXzUb3C6Devnb6fCC4nGLIgftthUqM10M\nM4PftBf+ChI7ITqGDOM/6hV4E1yTbbMlFjHgu/A9ESn2hlhYy+ErpXqbxW+EP20g\n+2KbuZNt82Ly+/Inlg1dcj1FiSFMGHmaIxiOso8NAoGBAIXvdLocH6NhvJxWJrQb\nZVLm36BnErR2fIraNh5w6r77W3rL0yZ8Gdui92QMn896oDX62t2bXVqkJ+DyCwKJ\nY/OlQq5GjdEEIe5mqvOMO/q68CZqZl2mk57Ajb36p+wMtcOQCSTFE0ZAqQ4FAPHM\n+6P3fPcdng5q2YHNJlTPyYKhAoGAH6ZWmUUde+fu/yZFGJEf1CPpY8JBvamiUAWP\nPq4kzrp8f6VAtBjRuQzZzw+qdnsAYNIDgcQtddUVJJEQW1PA3IqtWFgR2I4DMo+J\nZWQQUyB75s2e9iXCO66f+T24QZGwzgVWrl4pTzd7Nv8ErLdobmqdoktubL6prtd5\ndUZInm0CgYAbC70qlI70gpL5cmTe+qE9mC9DTC8sZzP226DImyKjQyHnR4ndvIVy\nCuoDNMVEZe3QEZSjGsq2DlkeVjptoBdA7HS8F9uiSJOtfH3nLu4BlkI1bqjmxDvs\np1bRnx7qs+4hOr/jt2GS/Rr2IoQ8zdJ+1LhDtY5GeJXX/oDjts3/bA==\n-----END RSA PRIVATE KEY-----'),

(8,'Mumbai','IN','2021-06-13 22:25:08.006835','Radius Client','in-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.6.3','13.233.79.2','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:25:08.006835','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAzi4nJ46DMuuE1/aHCJWRZgcgAEkbTMwVlfIf89/9MYuKQeNJ\nb/dkxgK0WIWPmHLhSbaGQKqn9EjPfaez+mxpADj6VK1IVSZWTyEnKpWAnnylZ8+9\nvDGhKrfWXcAYes0tFUAyM8hA8PDaREdJF3JJEij5nfatSAgfEy2yA+SWrVOV7dCG\n0aDqHE3JwcenDN3deggHxmGWIZTLusxLvvyvdTH9N5VN9Niv4FZPKFRhjqEjyZ0A\nCI/Uj6cylc0CJWf2MgvzkO7xjl+YRYsMU7dvIHORnya2pyuFw4iuPetJkz0khzM+\ns1J2BXDm7/9dNL9hfntC2fZaWJaBY3++nYRfbQIDAQABAoIBAQCIEKlCT6iByDAJ\nHeaLYblsrbMOvTBJBD4SkJFJrj43njEFvmtOeuOEUXCXIr1bDCVlMhFzR6xDaHGW\nKUB7OWQqA3AgxaKAnyZCXkysq7EP5o557fz234HO4TeU8xq0oAbqiU0YI0XIPAqv\nGKRB1ugmFeh1uL8uVD29uGBMdWSCyQAV0EAj7tTSNsz7K1xFD8wu2WhUupHBVLk0\nqng47t4kJkAZ0qBvhc8RQ//0ayXuSlSUXcN8/W0BB/kfnpMYi5OmQmIyrzuoD6T3\n4E8LtRNmlylc1LeykWEOIBm5DFyD4CQtNciaE4RgZqwC37pS+mCMYcpPAsymUz+v\nqWtYsFZBAoGBAPwF8V+uTRotVbfj4hJvPvk9mHFIw9wfQouc/LrW8C+X9cUBKHh8\ndlVdA3tnPyW1f56CdWkhAwqKB5+WxwscwIYUGKAt7K/5GTAZvXLQh9MwpuNb/zbO\nnEU0rmL7y2OepeJ6dEWDcho6OvkRGhOjpvi1bJs7/nWUrWL7fblD0xPFAoGBANFv\nBp8d1zAEfjZvXKnvB/6/7LXKOjBF1NiDN+PxWN9zhKrmcTlrOwYTsf+o0e6MYfZd\nGb1ct/N5+YgJVgu7awDHuKElLng7cH9OQaTCMyFd0enYAbiB7vdCVf3gmfbJsoVd\nh56LCpIbktsxWZQJUcegzjcyfrtuf7xJSw5q8U+JAoGBAMcmuxJs3iitYmsp/N+s\nYQr7jZcXdQ5g9Wj16RCMAMjbQC8pNx3e3HOgSLwfXlsiDXLojHdCyvBtGpEVyLwT\nQF+zSbJqF/sG0MydyoMMHgjZDUfkzsw0gHB/rL4zUwmpuFQ7h93WfTgUg72FsiGK\n1Mr8MpKI2g94KXtqWHMqEYYxAoGAK/TMg/rkXjyjLNOktDnqCKrJiMIgAl+1s5ee\n1GWkTInXT3M8UlvSMI1Tr6AEJ7gpG2cg7uCV7zErkUS8XYwBglFoy9Gc9jkrfFy1\nZFoqFxe05gqjGdGTkSvIFpfcWLKumFUwELOIDXBiG0U5Utu94VM71NPxS29+ucr8\nSNNjQFkCgYBmhv0ogNo4fMRlEjipMAfqUz9Fbi0fqAOBKC7W/QSVoGHLtVTpAA9O\nZFg9H4H4TJekkpWeBR2ffahNQvTfVVMAEK0LtkyhJK0axVfpLs5zy4JzgJ6x7oCT\nO1D0EZKvX/BpCnnHwZFmsvquhMqIv6EQAIPweMNHwDSGFB1zUWGoJg==\n-----END RSA PRIVATE KEY-----'),

(9,'Osaka','JP','2021-06-13 22:26:14.146151','Radius Client','jp-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.45.39','13.208.251.171','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:26:14.146151','-----BEGIN RSA PRIVATE KEY-----\nMIIEogIBAAKCAQEAmBnGQUJZrNaOpLAMLdCOn5Oh7N//aPLjduaZP8TwarF0lgzJ\nABdxyxQnMCIHZEpp4B3LVgHbyrrxt+V/1DMjr/Xg76vcnDnWN5ftR4Xej9Zx7B8O\nkaHprlssKFJuBTImrDyZbpD9Mpuct4y7tAERdX0zB1jvlq62fF4hAEvtd0ulLaR0\nNs7YBdOlfa/FAtwPunrczO0eeET3Et+DQDba2IYkhXsM+DFKe+UaDYEBIulz+66D\nSpIR4+4nBLcC+od9iDFwweG5dUnUiP/UbopXnH9K+dnGJE1FdLrxSm/pQOzIDfqE\nIHMIIzBp2AxjqK5qVb5Ad8I50QR1Gv7X8MJxDQIDAQABAoIBAGLRC/7F7qDd3Fqe\njasOPjrqnV7ZwIQcmS9cbeACQPfwRoBd2hCXKrT3aOZalVbRCawzbzF3j6ZMglIp\nZj0k5GCja/oKPA+7e7Mun1fCX3AvsF/pZXu0J6/BSBUCSLxTctCfpkcxJ27DrrOJ\nWUF/GCqupoJ1g2LwK1rthJ8+tQ61LzcfKgWdhF5BK+hUO26VmMymjYSsZWPicTsP\nkYuzxXwA/9E7G4INY7Am8k95A2Tv6EF3oQOL19Q+u/bBOiHYfwb+7gigU+gd9anL\nPCWKf9hL3HO9RVaz18vBruFKNG57R/oQIm4RW5ffrxitOorMTer/YGgZrU7hrqST\nFY+j2YECgYEA8GVLTsaiFCMqzhYR/pGOvPUhxH4gSzcwPvBzmGgHXa8TP93jb6Vq\nhJmo5XTeTEc2RCGRQ+wGcSHAzcemrVS1x8ZMAyXUg+M1JqnzY2O0QNHZX96Msrt7\nA61jFKXoBpxCDJNIwlFHEHz8qxNHuXPIBzPbH/fBRLwMEPte3aH8wf0CgYEAoflD\ne3v8df0nQliSfDIFNpIgHpn+LOYZ0ryZ5bPA/wVoMSgAZVcNTRVcku4EXkQ8xQms\nfH9O8QyADiIuSJHzOvw94LcnXEOtvAzbwLQGJQMq1o7vXuDiPDOVUVn1EutgJo3A\n2HBnceyHUzcHGHnDEMcEw7G9FHCSGO4C7jryUFECgYAUD4/qQf8yiQDAc+0KfmEX\n+ajYueG+BRv8lJpA5KWAtuR3Vu/s2DP8XspOY6NNYD/yDY8OyPp8pGFvirKpEkGd\nKzoapSSH4QUFdfc+mwpmwlxOeMbq/2QzmVuEVJf5PT7xx7SxqpCwmo2HRTOeEUBn\nAXfOwiEZuEuVezSecC8kjQKBgAIkjQr+KenZroJHL7Dq7Hy7VCdzfBRV+vN5wUNz\nrFLzXFyCHfc5PpjNdosPEQm6N1+X8Cs5d+7ibnNBaoFVRPV7zsnBQokK8CpztRiI\niShGMXPjMjF8IiIPwKLx4LceXAhGic2TNxsfuO6V5McjHTyElYCNQmYZ0yT3SRyj\ntP7xAoGAS9Vk+CnvX8CGwuCEgANibiYR5vENmto8DJWFDNtQZ6MHNBhPksCmDM94\nVW+gYNqiEmPi4FwcPTS6XDCTZbDsUl+txPd/zt2Qri+1UtzKBPSry0ZPdGoPZvuO\nzeViINCRARasc9MdgHQ4cUEbUxgaQ3geSKC49ZU/wYNhlZcAbhQ=\n-----END RSA PRIVATE KEY-----'),

(10,'Seoul','SK','2021-06-13 22:27:26.745636','Radius Client','sk-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.44.152','3.35.209.228','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:27:26.745636','-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEAkH2DpbGCjkCJNBIuTp5dZBY+FhT1CP1EFbQfB1TpjbQ1rZGY\nX0D/0Hma2cB1yZVzLsO4bOjyYc72mPL3Cao10HWPC3f05QJgVl744xV4LqNm64aK\nuQfRS0cWMxYrMrhoiTPajScoL+havrhug2GuXEDT44V5dZZofIk1cFB38lNqAA+K\njbZCoeixstXzSwrjjJ4NoYeCRk8xbNNsQabWyxsZZje1EEO/YNvVbU+3lvSLyhPk\nTwF7O/ck1FI9wW6u+uefZdp8v1zOv0StbRdE1bh5nFsW2lfimOgbJWAWQlnTAStV\n+ZrBnRROBcdPsfIkb2qoptbgPu4fqMNSkwl/swIDAQABAoIBABMTPuoFAUNeab6i\nF9LxPUsVVKT9p0vEg9SJrv78ysiWXe/IJOGOZzdSlorgyawPOuGVnSdjqXwYIp4W\nlV6FRg17N6M/9ui045sDdsAj+XXOxKubW30J9yuE+v1Lk/BiQAq8+cFgxgFtH23G\nU3Cs2KP9OvvyhxJrBOhMBO4K8YrgHQaQQ6f2htYChivyzFCxIagBGAffcyvOaJcP\nc3YwKOTi4M4WR3MR5/ZzGVOXlyJwPQLSESqND87NRy2TxnnRFDLKZqrtkJjKmkAw\nFZy1DjwazvrCANRLiV/DoCo7ZumwE82Azts0tkOny9+efc7S+N9kWJHv8HZPyj4q\nkdmCh8ECgYEA28IJIbtcenSYXwu1N+ZjiOSaa7gguxonyvm4qZcVwmFjKQuNidHh\nmRgMsMuAtVneGS9vA8NAXzjdDBgs8AR2G/mrCcdjSRdiN6XknUYeZ6MeDF6qGh5k\ntWVaw/9loZzcL+lzynHeMKixM3ws2nkKtMfMFPWnDBFaJYJDUClGMckCgYEAqFHA\ng8y5Nf5nwGmOFVt/ZjiuD7c8FJkcf/h9Cb+lG9acbkEgmJAgQUI1hqsDW+Qamzo2\nc/8zuYumhj8TjImAB7Sz9euB3iJ/xFigKGQzXL/E0xj49XJSo5OB+wOM02p2QUkN\ndaJhCPRxyyOhZgi1+RzBBOO1f3z8d6qPKIVUA5sCgYA8QAag6O2B5VWhOWSF3vSL\nI6Hyxpiys4kVbUHjvTiKAoldR32Wu6ROhPl1v1WaYvkxjn+xWGbdtIlmN3qak56O\nLPVnyPK/p1mWSDVVqVPMtjwWyB1667pQwWkRM4R5urfjqu3EI1P3o0hyBSmG/Iii\n5+RcGr2h4WAFyKEL6UUFgQKBgErA1BE08OSr8gQgXPvbQP9zGMreBA7Z2TB0eYPE\n2107uQm2XvWivNB+ySrf9IPxEGf0OI7Xvm8fOHvSs6nsvzGs7WfAilLPW5MmESsf\nJTiB6rYeFyNa/JytPELw1rBmTdK80P93eETz2z3uuxWHtNnIDWlQgCD4r1+uHhpO\n7HsVAoGBAL0ZhD8ypiik5TaVcAXAk7rS7Ogy5gbJge/c1feVE+KCAtc7hUAnPh6b\nO6+vCl7z20EL+mTSr3Ku3ej0xh2nX/M8oPk6jg5gO6ZbSdDk9ZD/bk5ucxvV8zsL\n5yfE2gxV6xhcrTlZSoEUgpQXPSUKnDXWMo3hkadKrFQ/U8ZC684A\n-----END RSA PRIVATE KEY-----'),

(11,'Singapore','SG','2021-06-13 22:28:31.694074','Radius Client','sg-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.42.31','54.254.183.29','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:28:31.694074','-----BEGIN RSA PRIVATE KEY-----\nMIIEogIBAAKCAQEAis7En5CsxtaA5FCuK8fBLCdkHS2hUBSeC1zWxD7OFtjKmlos\nEv0bgwhPexEa3p9dkobNK+iVzp/xs0rUGorEIFrnHK98nK3gbJBcVVludm1bwtLN\nUuroaAvIXkSS5UZCXv63O3V1piUDfgY3zgq0wMY7XBK5b/w9YeVTmUPlta/ciyuW\nsS4poFpkQQ+ULW0o8myAR+P994h0q4a1HFxghbJVJEvSjSFHZnZCzCcUjclwh6Rh\nVBjNwZ+QU5Zjf02xx9TeLse015MhMa7+cTb5Sq77HCxKLl0PXmuhVkQ8WcovYhkP\npyMxkMc+co63g3K+53tT8yHHRUepk8RVtAav8wIDAQABAoIBAACvIjLzIJbeScZW\nS3V1x5LzIsAuEGR3pYvCymAvOw7LxLvcA+mYC8GqkdG2dN5yBKELPwmfXNWm3icX\nEFKNKohKSmdOyhtvk/KVB494IF4/Dbu5FD/7Xmfc3wDv75Vnsj4LnVWSr6DA//Jo\nD3BZhyiIEZTTlsxhft/zpGTV+9TYZi7SOb9tcSmVEhlmEJLAaw9fpvwPIgJOu6Ps\nbOBFQ6YN3lsOyLXiJX1OQoVq7YlJG1IAWQKinqiBJaXYddKYx8vhHvsahRMNFrUj\ny5RcHxLwDtRwltCvGQxd1Hqry2vraz9JpUGCWLXg1hl3O3v3T2/oGx2duoe0Nu2y\nhYTm08ECgYEAw/j9v1zAN/csFfc0W/j1CC8X6pqVAPWzn9MSdNA1Z6NCO43L4aUC\nGQonEkGuh34+sE8PJ9AzbzeUv89NWmGEH3TPbMtM8OsW4ZVrtvZuSAgCTTHS6MRK\nQStUfflFHbvxT7dWskF0h7qQ6rIngBUp7Y3qFYtVE/n+snUmrHKO06ECgYEAtVM+\nCL57vh/hrdFBe7gK5xIF1hqXDuMgKrcdOjKtxeheVqyUu0aNDYHCP9r1BVy0Rt9J\nIPWG0ifq/bL2FCrId6WDkn+df09tYYKAh44K7KgE1rwMTJdRoXKXqB4qozPE7V+M\n1PPeCyUJKxdSpju5CwkWYLTNYIYjNphQnD07GxMCgYA8f7mWQgP1QxERFufLHOhI\nRoGTo3Usfq2dQYQJG93gyL3BtNykJFSpdkDpme1PVuHze/SpzdSTGQ2KyWyP4lrv\nexgt9fUiyvCdY4uFSD5zbkWHn0IFKdPimoXS/gkgTM0KVwcksKBQ5n+mdKlNGTuA\nYrdJP2JccGj4nsH5qsP9AQKBgFDUtd+yAnmjvrf8g4OCbKHLX/ysx9AkIa/JB2dt\nNhf2WS+sKXU+oxx1TsGmevmT9O1vFityKFLA5rmHsLkWxVzI1NlWuEALiUgho+hJ\nwsbys42CML1n60TcT4zrPbiZGF107dP0jnRLY0XpVLe0Ae5Qc8BSeQdhZIsu5Shx\nB0svAoGAC3YS4VDONDOEoZdYJYO+Dqhx2FvTYkySthMlLAbUZVpDPmrvFxphzeQG\nV+XM9P/QzC4bVmGCy37TkiFAH2IWX6kzdT+8R5zYxSOGjdIZOjgpUKk/m7QDpf3V\n3vrfA/ioKANISYBNLbk+/9w0keyw7U7kIDptlGM+/7esVjYdzaM=\n-----END RSA PRIVATE KEY-----'),

(12,'Sydney','AU','2021-06-13 22:37:18.177728','Radius Client','au-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.18.42','13.236.208.66','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:37:18.177728','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAnYithAP7t9qHshhxix/Iqql6JeW7OY6gJiMS4v5Q4FtKrqlD\nKEKgwkYQR4CFBYyne0GqS8Uh3jxjHDmSqlsX63ZqiypDWKTiv3GFJRAqyMpRpK7f\neYcZmGEQOUXonVNu8Ff7LOn9Q7XGhVa3Ywutq+js0KOlHN64DcPi/sJoHcl/vmok\n5Hjq77SFPzzgI1FUXXWvhjNLfmpHmVIjAGFZdUZDunqhIqsZOhqzDVuhfGdp286k\n5G1Pi4DYKaMzHRwoLuwc9b3gn8JzUtupSyFGHVWPYxFjsZKIm3zLdDc9uW46sN+K\nl1qTvWO+amivvRgFAWnjLFv7MdnL+0SWCfRT7wIDAQABAoIBAAUXU7Q+hLYOY/BH\nhfPfFRce3wm2gPR9PYO+AnA/k9GMYb9/TOJSNOvSY9hLuQDS6N684ooMVundvb49\nxhWnWACOFSdmOEL3ITTasFjgSuGuWDEgTayZxyP7jxCqx7e96Inv7fgnn72+dCn/\nAXI8AGLb5jtf7ogeUohvWjitg7viqAivdbyf2hVsOx1NNYg8NdOUY2MqCQsk9fln\nq8XRDzY+wH/Aa+9ZuTXGTZNGyv6g2TZusZ8ZC1wnZwZs4yj9NYLEa1F/Jx5qGs56\n2L8mQw5V6Yr0IoKrFlUxLTE2p+Dhp7UgOiCD1NfrKCgkfuuNa664rL1jQTZi/YVt\n1mrAhAECgYEA7joAuzUcjLT2Ju2xM1ktum3bCcr+uZOoCsNkH3OtPd+P94EbyH2z\n3U3tovmtsleLOkgw5eKlsb8Iq499GdDa8Wn+JQoZwYXgY4i5Lf1933wgETf6nSWH\nG06jYaeJY25pRwTCsTXylV2gwS30s8IRTWQ/UmyCoEyO2malwpABkO8CgYEAqUl9\nLyyha173WoHan1HI8TqESmiZ1TgdPMqcZ+6/ljrSu5qt7quIsTnVJiA0jJXFPXFM\n/9FaDdHv7vrauhF3yUPZpvLWFjWI9GcQceBAQ4Fsn6pIJ6Dh+AYUWcw2yS+EhJ7T\nNaJwSk9S0FuO7jIZ6C+dspXJkdA8sTpJwMqTbQECgYEAgrmriw9zEjd/WA9ZDN9j\n+4irCO2u6DEBpp5gA+5bY7eZbe0YTOXF3KwVujgeu93UVfAlfRDpcedtn49Gy/LT\nImTShIrNZWfMoXH2hCk1eHzJsAky6iccw7xcZYslFj4RYVpYaBw9eP9+5mMMg9P7\n4KchJF7NM5+vSGPJlEGnylsCgYApYtP98nVMIJ1sPQ7LoFz1zI6G+meRXvSW5hmy\ntwWTnrHiN4CKNtlmKnNXrMvsCrl7ufVPHUlc05RK4n0SvC6Cv9m4HOoF5/She4jj\nriVH255IsoKNXaGg1yumgGxsZveIzKNqG1pEcHwYgCnDRfBbeTiebkwnH7eWxF8/\niMbeAQKBgQDWIpEjy++aO30o/S+aMpnrLHqk4VpmXdRL6DGUJzTXs+NOMUADPQTw\nhYLVnF0sFWb8Q7wWCjDaLeZUR/z+PtmaxvFDl/jT3D7uXFQR+lZb7xsUqk1422c+\np8FEXS8xmc43265J1XuZSqNdFJBw7atHytsfAS1zu9fA1gb7gOFz7A==\n-----END RSA PRIVATE KEY-----'),

(13,'Tokyo','JP','2021-06-13 22:39:20.741595','Radius Client','jpto-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.43.176','52.194.225.49','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:39:20.741595','-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEAlO8b7IWof6FHOSDb/P21ibPD66+wF88LfawVEhbjdf0yoXuc\nt6CWmoOY27fKlaFWJeh+0lnUb4nKTLXjNwyhHnPt7N05nTHPXs2vjF+CD0bI4Q7C\nEbRi4cp0JIZ2a01d5YzdbJkKs64texWW+cPTnwt9wtC2l4eOrDgcNerM/0ezV2G6\nBrNPBqdxsEOh+hYfs85ydyR0vrCf5Bc2JmhK0eO536P5PBvTbClHE6iOoz7nwLeT\nRSyzfrzsaOPyHaLTbsESoHgO8p6uzn/pZDmNo89KIcLNdwWUNy//So1FGuubh9GO\ns3/yMZGKlE+PqHNoea3ZefqKI8DVDlpQtdPXfQIDAQABAoIBAGyLZ/qV3xxF2rbU\nMADOl8Roc94OCXUQvPs2s5Sfgd+4TXJLuQhLzfeG+Z0I8mjjUPoH3IZKWf2AmyQV\nXCf+2F5td3YuNZNPfidGGS9hXFTt7fG449Are/W7q7IDHcNOe6SVabuAPsB9rUAL\nmCEoi89afKnDm5AMh/sUWdsh6Y4TAkk/qPZIAOnGDPyX/+bNE/WCKyBvLqWfoyJm\nclc1Qdx3s+65dZowB9NPpzCRusS1KMWeP8ZgNEugE6N+r1nLWdnMjeYcCL3SZVbw\n3h9YZfaEdMU5cqvaZJ85f+EPRjOZzbdlAw5TY6fXW6xvHlNlX3wMGQv2rphOUxV8\nrvYteoECgYEA0jCHdEC+x+U1BzJKcktDrZFnwTTHr5wHg+4xftb6+fsfKIUCjLvJ\nEDxmLk6mQAneP44PB1IoMdVUP9/biYBjVr6O8Cxi3ECL4VZBVPaxj2EttapTurmS\nrBwUCsVMJiB4Z7jxTBZgftkIQUJnLu7HV/ZDxx/bcy4CPCdddEcnqakCgYEAtWTX\n0RJhDhl0sfRlTJa4O5+Mkj6vGYkPqwgiGuJkSMpIOJAtVNA3e0jZJNEjbvVknYGq\nVw57EeGOESvIhwMGT9UOvafY1St8VB6IbjkOnxl6LeqBUXD+GxmY3LA43v1dm0Pa\nRALFsoep9i5/Vau9Ekj++rRsQWSDePWnMH/tq7UCgYEAhyATtLT68V91G89E9P1V\n4PudjCP2S/svCa3TvAtRBexfN5lq7ja7J+jsd9CwKHMvogQdvUMf/Al9RCMR6+/R\n7S5MjjK6y4XjZ1YgYhLGS7rZu/RoJ56toF/csp/sOIafYW8QES5BksrL98hMtWtk\nVDRJlnXwireFUE6uUdZ8tSECgYAh0eJ3JaEY2uAqCbIVXIiO/UldPviwnyBfuA17\ny7f85lIe0arSBFxDzSpHNgMQPgp7X2CeCKIjjFVLmEkz+vljSwsLi07fN9Nfb63q\nNxMl0L857l4cDCAkd1pL00NXOBAK0dTRF1860RZ8QuEU3QdMcHWx0o/YhWAJ3mtE\nnKV4fQKBgB3tGz8A9C3hF4510kjB9bHGYDyGEbryJvzheQsZE7gfw8g91nYZNPdv\nCc/H3cvIbRq1pzrQoeqyC341xWYL3+jQyef5QMcI7UXDtrSyo8Q6JZZmGiGBTbZm\nyu13opcIfyKdxfKsYqPaVIbr/qyXkf62Qz4aC1WzaitbYgm3KKPq\n-----END RSA PRIVATE KEY-----'),

(14,'Toronto','CA','2021-06-13 22:40:47.781043','Radius Client','ca-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.24.224','3.96.216.2','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:40:47.781043','-----BEGIN RSA PRIVATE KEY-----\nMIIEogIBAAKCAQEArBfaQmvxRQWIpJ4v9sWp4rY0d8uxeGocySOBB9/sUGUjsdxK\nmlBrBtlHhbDF52wfcoGMKdCxBsQdWZ0lYfxtVbp6qkYzREWSI6+gCiFIHJ/lGS3T\nomNofJQTlD9MIpViuhxQM0AHHltkmARmprx+WrqpJo2GW1XpOauXDks9j8bugdg7\nyRsG5sfarWcRspEpHguKEfINtxHji3oPhflqGTMhfdG9rZC9VzEBAjHuMvF2RrBi\ngpyhqliqt6ehv99yEBmNXsAbH+y21j0+dDCb+ujy/i3gRWqyx7FEFQuGygAfOU1U\n1hvHGTF/jB5z2NfYWPT+h41FlM4XQUURnHtzjQIDAQABAoIBAHn+ZrCTSHN/WsAn\niyTKNoYKV+NcODB+OG21h3CHokPTjpw054XA4D34pST2jTRih2BqyWuTDwsW1x+B\nsNBnWzJcgTxXi1woXVKC21aTBUWVa5iPzejdw2Icrkx3Goehkl0QWSIbj3BLpWHG\nLOah1IiIrgQwa0AwBKbnJqvxVnCmjBnsq9rbFdqy4eHCOwHPuz6BlJgfAnjXqxdE\n1g2QsD8uw+5uGCVIx9I7yd9dFj7ITBAdrrlBTkwa+eV6Nb36Clx4y8cNvEQYa+5M\nxKZYp+z0OSgJ+grfeZn/0XKl0yat+cRqZVbrfZnpMVVb85930eVzzIqLeNyl6vh9\nSTvOYkECgYEA/IG/tNQnv33MU4N1oRQenrL9wF7r6BnwDyLoOXQxwkT+vQP7Hj0w\nEbAkMNrVJkQeS6G6X+IvAMG1p53boQ/cufovYSPaxiyZnXjFQZU4pr3mEZcyfjXX\nEzDMBpCvlBMju+sfcK/VMCv37SICxCyy3naSWVNFzy/BDRT3wZl14ekCgYEArnlR\nv3ZtauMOCiFhIWyFAW3yoHYjBJXm27iXZ8h0fo6dRTnDivR0p+7XUWjGq4tWhIm6\n70TmrrW8cqkvzKs08EcBb6UBgSfNKg64zmxvKAvCRsfCcxruPnpVPy235+7qwabI\n67xLydh6w3nC8Sv0Y6pCMHtlTH5Lik7+Z+M9egUCgYA35NS1x3yg88zF88SXCmgY\nwxBOg/R+ZTdxck2rYiAdm74zwuzUbcD1OWNZeSdTmCKIpV0nGl9DMJ3wY44ZYfR/\nKnVQcGpeEcNTS0eG0+w9ZK3H2QsxSuO3+MZA8cgb4/y1/T7xZ8quJh10XWH1YNub\nSO3uXfOHAFAggDKzpS+l+QKBgAMz4OdF0j5bCp/u+ux1JH/hN8nwQTZNVh6EZesW\nx+Sm04Y4yDkdwaJvhO5OoQ73ENCAArlvp1hfp6TnLCRypBchBoq5Gaz4QRDbklLf\ntK6kgJ1x/0TDosnszvHI8g5E2XRSjm+dVjrPSXmbG5P3vZWyw2JGcZaU3ugcCRl5\nF2DNAoGAbfAqZWE1NGxcaQl5GMe4c0WK5Wir3PpHub/ZFmTTBSZOEQxC9PUGka6D\nwGNfga4+IRJA+G1VDh5ZnBtFjnbVdAJHVMjqsGSjnwk1aaqrkKbC4MsSRkpkCo31\nAEx3FWAACmfXr+sw28dinqXAl86glqdIZeBIin0SjnOipfaMSDE=\n-----END RSA PRIVATE KEY-----'),

(15,'Frankfurt','DE','2021-06-13 22:41:54.070640','Radius Client','de-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.29.92','18.184.52.226','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:41:54.070640','-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEAiubsy4RuOx7G3x24IU3WIV79m/jqLRzDQLAtQ0dh4mTi0Jjf\ndxMBhayWBGiWDwx/BvzUMjIa48dphNTIH7922PU0Fz7BQ5jxgExUAslw5dQ7tj90\nVOCSwirWqS8IswqEIbZ63V7WohLTCPfbW+O7pSV9jPWrfqzNWkwwJxAuy09MPRLC\nq8loqfrQys7HFOU19KsrWWK32iIsQdlX94DG8lqOlNVlXXAKGlOQ1zrWPWmc5sFd\nxX857bj7fFRmRmZoEn77fhbPH6rN5FMyZX53BDzPdZRXouB/rD4LV84VFNdyC7tx\nvMroGJXoSab7kWdpP2Nol7xT7d+y4APMCMbV0wIDAQABAoIBAAFQZfUW76QHioIZ\n2eAWyGvQZj3PgQekOgSmpgb46egmZC7tA1AxEdad511rT+AckicSxT8kUjTNciYb\njTkfqpQq3a9KnLFzBPXdwv2zwimqdFYpC4SyCzFjDlzTYRWwh8d53nexqf4mh3CS\nR7T61o+dZy6qNFEieV7dUadM8+Rge/oOwxlaxzHpwgKupTom9mrxBisv/Xqt9oih\n/Xh8rms+W3Y25sl/Atn4Pk0SRy+dq3I0DsKsM0J72IfWoMqSFu9vsv0x0HmBT5Qs\n/q/wgj6qYJ722fnX3S/g7AklJDbP+FKpZM1mWRCMGf3X4YZzs8DcJnZQVGM1dG5L\nWTU/1KECgYEA9j0bjecHixxuy/wambtOjeKMD5Z9hD9nlY5HYy4K7+aEhZtPznkB\n29nwEhKXpOlVt55MMiGGjqT0ZISHAfi2vetKkGBTyE6J7bZjVUPitJs9uHkE5seu\nSgSzagHi/sfogl4Ccceq+yfkaC2MYMunswrXKoe9bIGcO8fBwE8DZIcCgYEAkGiJ\nuNl0A0Et6IxeVKjJnwbKavONZAzrotewd+SmMEOroK/gA7wMl4y8kgtq7ntc9DMx\nDm+MhDBc8e/kQR/nMpthqYItsTbKi1VWu7aV5xYorLq2sPt3+k17zRNeI4hQvQsn\n4RM0YQJhLLniteL4m4wfhBB+nUp4dzLGYVzYI1UCgYEA3uI3I73gBRoqa/d5OHfj\nxegvMRjd6iveHfBYS7zsZ3P3tlv74/+CIDdn52Pu4XXXgcoGXEYeXtWO5cFT1wQC\nnWvx5Tdb/u6gSIwT46T7+tCeEHFyb1aFOziAEc89YbMfkYl4mvbJnGbihy3bZ+5P\nkG+ajUOqwSGcgjvw85pRjtcCgYEAgu8mIIKaG/FXTMkpJp274gLoOb0G8DZ+2LG0\ngaDBxnU1Aoc1GG3DBQ5IVCsiE4WrLIfV7P7ba2fmfj9CuIHSwSH9lRJBl7LURy1j\nbOQKWvLOgn7w8tuvVzY2ECWFb7ZDboTU7dDZ5zOfJEyO8NgRDggo8zd5deh1/a3+\nDX1/Q9kCgYEAp9UH/UpH66e+W/Qf37ySA36DJ/uUJnbjO2Zxk31v90MkV92hemrR\nOAdkEn2NNRYn6TLQB2Oeraprv+3ObUicegzfUul27dJHfGLtjmVoT4bv2soTKHWn\nBh5Xthjfgit7QR/MkVKsZD0o9Z08umER6ClPiaNkyPFkBYu7XnABtRw=\n-----END RSA PRIVATE KEY-----'),

(16,'Dublin','IE','2021-06-13 22:42:58.167568','Radius Client','ie-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.12.96','34.244.240.144','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:42:58.167568','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAwrk43S0nRnokQtgW6C2aP0HoEvQWTlAT4TOIG+l/3NyaGAH6\n/pbivBmvHoWM63X5l2s2sjk2X8nGb9L4kOGBZBRhi6yGjJTfXijKZe17PCO++Z8T\n+oIrMVWxt9XRgp2ueUyqiexDKv412bQXgr7HIymsvN8/8D4PqucrIvKk7hblzQws\ndmpin2oTYd29hyk2fx5xOoJKRO3JSDXtvSagN1hcD5D+4KiihTKKPpFPVF0/DlDg\nAFhHlM4XMN0V74/7CTskUwI8OhOsH5VvlmBBUbxpDFF3WM0a7iXKIOS5+nXUojge\nh1JMuNxQOl6px0H/Y4nXNIio05EfhHLYoYgkeQIDAQABAoIBADpYb35uSidm77iL\nsQluqJotsFEFjC149wvyeeULKQHPo+hvp3pjUZrVA1Dc6WWgGuJRALmURQOcnPXi\nvBIFT9Th+nx0fHhhHzDCfireZp+VgmKfVMgp+rE0oSSZT8aYYq/71dmvzkxykUYj\nHSxS565UjWqmZi0hG2a1D4rQM7UW52sgIgCd/fmfxwZ7Wi7QUUalVI40I36Hgkqy\nGYrfFNwW83n8o2c/SJXzBVrnoYu+MAd/dV3zUqaqVAjgf+7xZ8v1lyu7dQaZ+crc\nnOHclEd2orJA1QCIFgo659LhXhZ9IAVZl5KLIQa0ZWNrqIpxIvu5bdAW9L23ewrl\nwmha7ZECgYEA6EDPdUpLsNB0P3rBDaHhmInHegevMGW1acJ9jG6g7SrZaljx1eNn\no5OFrqXLKP/qkwl1zOJdSaMBkgAmRnffrso6Fs4RSHoedpGz8Y60/3PZGHH5SWwK\nvzMlx10VB2yxIUoZF3HrlNvm5PumTYfGDWHXol/Y9aaVv5AGqSELDs0CgYEA1qIU\nUDm3l+I0ndzVk6SjQA5csviRZoS+/dYZUThMu5/ql9pQgVBXHwYshXpVbeVhJAMW\n5sY1fhGLF+oqf/OSOctnRW3btKOJuu8W7scHBSrvedbT1A8IBL79bckGAig/3SV9\n9hhZ9gLFutKot4dv/DdSyM1652Dquu6wZvVK1F0CgYAq6n7rP3VJENvKNxahPD6B\nf0Qa6nyWxtSE3Glyn6ag7IAMRK87RLAL6+CR6xTlWJSx4tefqlxulO5fWiO4dcIh\nfEDLofxTli0236mOgz4yzedZuwXDaXF67yqV8j9GhfEdI6ILUQOlGBVMTqvmO6Z/\nJos/99IRYlzdbvGsIuz2KQKBgQC0cF4Tc/JcxdbmQBltniOAXpKgA0h5n2/RhoSS\nO3qG4dIfB3JWxGv5sP8M1+OqGASqec5vBINEEQuWd05zcxmjbNTNEaZ7HmFTvlzR\nMJE+TvI0w94hkOF02/vCqBVdCullyXczxRUcNQh2H1Ontzz2FKyME/MXQesUbEXw\nyJa4IQKBgQCjjMLTeu61Qeo1p9dp17LGfG9GWPRB4PNqSR0dDZPYS/PINSDWeeRo\niYNt9/sGhmK08j8dErWYe3fNyZj9TAAUyT5YsjK1ZMuYeuExiG4+ocZyC7zA4pwM\naq4frZWZJbVZoCii6Za//ltWpij6XoXFjwTPf1mzhzUxJACJS5grVA==\n-----END RSA PRIVATE KEY-----'),

(17,'London','UK','2021-06-13 22:47:22.512046','Radius Client','uk.iraip.com','sudo occtl disconnect user {username}',22,'172.31.14.150','18.133.184.96','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:47:22.512046','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEApnDjD8MoK04gjgQ1bUbhbmV7oSiUfMjNnPsUyUEPxPQaPZo5\nGGe/3q642P9149pj2azxcBZcxNXHnt2smKJyVL0WB24msCLM2wkor6+hQBxDeDNd\n16qebJb5L9OorWUC6xU2MdZ4OIWVJ2iUDlMxo3zh/mDg/q8RpUW3wXrymgY1O2eP\nomaVgj+WWgknjXPN2guKfGTm53tQZYYA5V/l6ce8RafWBQc5Ss/XJrMLVZW/L+4O\nB7JG8gMDWsgtQ2x5ZtOHwbuLLHOp9ulSV5t/fbu7OXkibRGwHVjWHt9k44l94Mlf\nOI1KKcRUUfZ3x3mNKxwTXlOzV4htoHlVVMn3CQIDAQABAoIBAGv6boRqUqQNkncv\n6c1UH6n0Molx/ysZRrNh/LE4AVgAGQoABKx9mm+IIKm+yaehpgew+A2EpHiaTGAX\nZgK72Lp61VDKuFSqrwYm4ftgUJd7/pV7lZT1vsOqCk1p3lNnZcZHQ5p/jgz1NLeV\nVAAbHuf2e9MA3Na9kxAKs9TUw5ECBt4F/anO9nnnWUeAI9sMcrzojQryVXbRoZH+\n7b11ll5qs5hduo4gnrMc4jmTKbTIYJk50qb+gfu4Ng/aHc4MbXiRbUmmqHH+LfRm\njtlh70puw7SO8/9vlz75f1XXL63eDtGjP0a+IE5Bmo26jrXGlLX21qGvEGHBAmv8\nfbW/+IECgYEA+9z+hGEpSDvAKyoBThr5M3oil5f/mqw6bUnEWfH2ciInLVGXUAoM\nEQ8M+5KvVCmNT23TWzVxb4noHae0DfEdEhVfjhmMVdKJMsomp05Z1mboF7SnFQC4\nIpfMAzJHEsfaWo4Cz4+2BHMNtfw8Q4E2nnYivDItNzu6Sbo9reOel/ECgYEAqSy4\nB+oRY1sK+BhEz6THDGgRHCbkBAyhoEtWn07EtyZsXHpYe3TSzHL3HnhWz7JEb0p8\ndUrlxB6gzu1hSoWmn1a1x+qAWTP9uKzVJTqpPOAYKg8PvdJZnvetDA58GLuI0MBj\nxe2OusSd3edjwBOtH8BvVTTYvubp0Xnrax77qJkCgYEAn4eYMvNz+YvXKb/+eaF+\nLQv0cz7UqPzkOSsbUrl/F0XouLzQceMbR4bX+1Uq9s0m86Ol10Tfp/2a54W894O8\nnSKqltA54nZrbp86oohvQO2HntRPbG87MrYhg/jJqeyqsub2gtANQ344tgehiESE\n+xPq5neFylFrqp1rygO+BZECgYBYojVI8Dlr9KcqlIx4vbwLo/pS5Y1REc0cTlxz\n2rD1xAvB59ez1uEafKE8NZBGAQAZzsymJzd6KRHXWEdA5AUINkXHF/VmxvaHawXB\nG5rPahk+7+UvbqXFw73GM1CRJK/pruKXMH83Gwwa1sBMQgLSP+AUpDtqyVB3j4jn\n9rOjAQKBgQCYEHss43CAlWFwOwAA0mkRaA5bkjP3syS47v8FXAXV6MFbZCEw2Zaf\nSYdA9jlruFglOOrnGoMMygQh0DAqIeoyeg1TZT4zBdmVbk2et5f7qolGB1zY7oBb\nxJa3aJH9ooiYnNAmcvv+AgpwQEV/4XxR2XF7i2Kp3nwgicaAL/weKA==\n-----END RSA PRIVATE KEY-----'),

(18,'Milan','IT','2021-06-13 22:49:24.028300','Radius Client','it-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.35.228','15.161.46.161','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:49:24.028300','-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEApa1FNcFplJWqK17OK2I3Fh8yI9HbClqiwp51pSR62JYVKZYJ\nIy2xL2/0oVrA8CjHzWBGi2AZNXNmewC21YbQ0MHCGgDnNTeLgSXMJ70/iWo8Cdvs\nJZtx9QOR+LG04WRj710Kl55TvIYL/RLmB2+cnQxAgMc1Ty/F4I5vq4aZbF+mJFbR\nV+4Ccg16eHos6o6BpYkMxzLVcz+o3hy27scXu0RFffV4bc+D43lYzA7OPeTzJOUG\nOOo7X5njmieEh58czR9w7giU/eI8dEuIwvB2gQrgbbMdCKsB6OyjHcGlt90Pea0+\nwGHsh5n9uHMLVZmzKI0vwNPf6HcsD8MuG/8epQIDAQABAoIBAEmWVXhT6QZTF5td\n56sDW75aqtWYH3dQ3Jwe79gjBOFwgAPRnUtfvyR30KteePXy1xQUoCDF1UyVbj5S\nCeN7lwIyHXmZTxinDDKXzD8WYsMxSWuDoDbv/US7ijlxDQtToMOvisXfUAgbbfXB\nUCSbYW+sAnZwhPAgIv7jpRwFk927QyMESfSsjD1n2Eri0CI1bjHn0yMEW/rOYro+\nmxZe3X3VXnsuL2SuYC0akqXly/Qjukdk76wK9Q1EjYsGGT9wQ5pzjyBnOj3vnnmg\nsYLyuMm7ulzHzfe05HDe/3/SYyEptRSDpOxNsttJgHwMXJ1ommjQ+8ROKZ9Fit6t\nMRvwNZ0CgYEA1lmz+TmAydrtozScUUsEo261iR53LHuj3Lshu34cDkQbEnMIJfQx\niIqd/GMEZ0OPUIyhXAH1MRfBVHmYXY9SIcQUTfiTiJSj34dEfQMH/t/v78QnuO1E\n/ge7WqmiEHHb+IFAYIuEYQzdc+0LuyIN59kBXTUnIz5/iRaV6nI0GDMCgYEAxd5t\nyXwuQwMjWAB25DBaZoADnrilk7WaN+84vkY8I5+Ar0K2s/1wvDc6kp/coMatoSkC\n+MSCKAfTQfV6WcwuzmNTpHMngp3xEHJMYXkcLDQTLHXrTDCw+Dqex8zMtDCwEEac\nbwtJxxkCLOSgrJcE4z7pimbKhlGW8sOimlLLdccCgYEAtgyXs0soW8gp3lw3iz+o\nn9nOKrgKRWh5ImZ43tHbqC4lxASY3gtI6ou4ZHwAaGfT13ipLwimo1xmoYVoZ8Xq\n3qy3BgXh37VGfPlYrMgfSVQOZ1f02LYDy7DrabP65fad+bBk6dp0wyx3BRdFyFjC\n8K+43d3f8KoLDXZ2Oxllr0sCgYEAuW+SPlCl3mGTfwa4ZXQ3VHZGbjqn2j1IcYE9\nEX0rIUt7Q0TnitlLDOlJeF5fW0G3OSEBSviG7lMfFmKjLoLfnsaRfb352jyAziQA\nFx10XKzVB0bfpEDBqXDum1sAwSIb7rTuKFdQ5kk2s7H0Lqs9hmbQYcehp63RJGRZ\n//z19sMCgYEAh1FoF8GpUIWJDk+kMtrp66rXD0rFjfjxWIBmJ8/ADWLBVcPv8/ql\n8KDRUGy3m4z00gYg/XEymxVtvdrU4re+ylfSe4lR4Wn6LkDLk76kIFdv2wExil9a\nxp80FgzgBB/yWz0Xca586MO2VIDdank+nR6d18l7bKCbXyrrhvBpVqU=\n-----END RSA PRIVATE KEY-----'),

(19,'Paris','FR','2021-06-13 22:52:24.506459','Radius Client','fr-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.15.85','35.180.247.61','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:52:24.506459','-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEAoNxjqcUwfX9ZFtMFdLosbdEvMVN5byEvhUrR8pE983xXb42z\nknigXAVxBf95L13XXrOmBsg6gZeXfY2muo3jzPIPEBdDKolGTqf48UcXid3fyb3n\nD5TI8MaR0jkFXRhdMWNmNdfKyt212t/dnCqhTlBRM3Jyn0fWDoxYGTvzwbhzuVH2\ndomZIwdFYpQsdoJdiYcYCy/xkcmI1otc9yEeP3LAN+WMSgIgFL0+4Pd0Ga0zlvyb\n48wNrK1YyaJwgrPY/Zs83Nr0PBNrkw/+dyhkSsbwueXYnIATtiToUFduuXG5A95G\n7maZ2hD6AvPzgmCwwBWKaLeUH0+WXi/tnxqGRQIDAQABAoIBAQCIevTEMd32XHoF\n93yFkP/pHLZhr2U0eH+NI/IFUF2znCfDKgLVnyrPaH6AjEURfxkf7GRNFM0u7qNF\nj02KsqA4XpeRG2uNR3MzgSih7aN3AZzB2jGU8rOaNZ+7MU3vsqA/KwqpXDFe9cU5\nsv6fZTv+6f47HDtMEPL43PJSVp92F98Co05m7pWLHIu4owWoZtHAbFTBwyweL0+c\nU7wUsxvhvCyAHCYRoa4vIbRAcvN5f0OeDS+qegiFaQ8CjrL/ZmWIXadsFJXnFzc6\nTDiOUO1zHyVpMBVTE1Se1KYBCEBcQTa/0LThI+PAUlk8WiC//86xo8+0aW01MrXT\nZ9IwG9jxAoGBAPTp5xgVW2njy6Ci481815Zkg3JEve06wtaSvuEEzlKUnLQZCZ9x\nGNNhJHqfeZswFeYu6pcTiQDvKPxW6B2/qEm7u83T4p2l2xfLtr4YAFQm2kgl3b3D\nPRnZQWbGWUhHRjeL5lF9aIh34+q4vsf3EtTt7xph8SAsDoMo2Mxh37tXAoGBAKgk\neFNwf3hn3gpq8c+JQQYOkvR5q23gHfdAq8tDFL/lSb6R2GEZVNl82L8W5jFAbZEU\nPMEZg0CaJrIaOzpLvYIwDMZWR7hgft45CGRv1aJmRjsvnv5YPIzeCwXU19tmEDx/\nBj8PT43pP02BQYDyj0vpfXJdQFJ58uKsMHLzduXDAoGBAIh9wtjdLjD5TwabQTHI\nHa4nHkqUnPSVBvjml5J4RzQmtp54RbCiKx/Cx72/GtquFcZO51aDAJuQrdrT5FCP\n+438HlI7f9Q0FsJQOev/HE/Jx2TmFYooQOki9xv79s4dSriyMw2/Qn6rsk2h4FE5\nFTmt0M93Phab9wqJvSlKcpHJAoGAZs3Vm/6ekXnAMtJsgp3C2fczNrLlc8qhx7kM\n0Lx1BVclnZUCnWuli0xgIYXYvKhN1XRwKQh2TZtyqVaMe/ovJlhGl0XNmBe3uhtY\n3D/ceOzbZ/iX158PdnmUd75JNGXU64gmw4uS8ot4HuU2raagoqNiBfiqCUNyk9D/\n1mixe40CgYEAuTQgxcas6wO9Md5vlAVWAp7zNlaSChRYC62XYYHOPhJSNZiMoFkF\nJlHoKEOmb4cm27XrvyimvJ+pY4rm2THMHl++avhPRVwQhiK5aC/iK5aCMoS2VAOE\n4VWm58AOzajqCVPJjAFMiVLXgclyUf66P9f+h7OafMTPstx665Nt0Yc=\n-----END RSA PRIVATE KEY-----'),

(20,'Stockholm','SE','2021-06-13 22:54:11.977614','Radius Client','se-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.3.103','13.53.45.189','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:54:11.977614','-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEAxqPoJ/9cAK1cFjR6uCz8Lpx5vgF4O5quPKvWOlqT+GD6Unsl\np5Cc1IW8k7/M2kOkbkGar9ovrF7u8EnpU5LNc9RFAgG+vP/T/69I50ZWW0mthx6e\n0gR/N0LJBrU8gevkY89jxmbZn4mBVnOcnn3soWo4Dij+aPrMrHFAGPvTCVilcbPz\nR0AzHH5j2Xj8FjFz5ts99GRaIGQjiVY/kVv/NEz36+2yQqpMnC+XjqkpgyiucEaj\nBgIT0ByP5RrZH+yyUBfHKWcwoNxMRa/LQLNKPjA3EAml9sxPGajMvrDsGPcXfJ2b\nQIaxjXn0Akx/zcvIrJISSh3waqFdrdunrRVanwIDAQABAoIBAADDiJT+yl/GSuBa\n+03R/nY/a8hrQQcZpSnej0hSKNd/p/bBOnfWeoPmr77xsdv0/qDd57ZdZGmJQ+nw\nntEPc3LKkilb0Fvxr+L/zYv2HUbklaI7X+j203Zm71bzQ3RMb+hiV8PfiV6vrKuk\nbim92jcUrXqAddn5fOf8135BGaKjjuNBPyx+f4+2pcu/sh5zNGXzM/9Rxnr7y/90\nG5LgA3aeq1XF3IzlPt/y/paYGinfVWAKGh87ASrbabLJvFXI+zhJ3Kp6NNgMIhT9\nGiUGGPXnuATl119bva6Q/xH6MkpQLC69P7DVYcyfesslz2MFNnpYwUYLt4wIeJpg\nHRzWKuECgYEA5fp2+dQPMV3IYmbqx+qEBCA90IyipQDki9+B1t2Co6OEP5QycUI8\npmf+OODgCUwNmX3uOeyK4X7l93wBJigd+M1Eh/zhENLCSdeWL7s7qxQeHnm0T+Zb\n2EUGFZwkQhLbROiiWd0sAfsvYtN5iTL3iUrqi6hAR1LiHXlIj9hieFMCgYEA3R20\naXWOaSYeH4Vbi2M4w++no7tKCa94gAs/DEaN1tvS7LgKKeIozaACF/xSg8vvJdZu\nmIH40vc6GDTZkvJoHc7uuLvkRiaBf0I6/vLvgreaTMlEzsAR8nR0WrecpXFSLRSP\nItQKzgrlkpPMUziyN7O0GiLkK2DfHM5CV+Ge2wUCgYAO/M6qI17GGdWITCq60pa9\n6hf9Ml0bIb7DOtKsXQ05etuQhSjwZp+rPJW9wTH4iSNNwH44+ihm360+/q7YfL7P\nSxlPP3SvcvzEcS9RdwNBd/Mqi0Xr+xg7NOxblixqve3aRMrnzlki2+rPpvaBsXee\nU+qLJXDi9QzVXOUUssZgDwKBgQCPnSl+jZHvjYhnJLCuucU2zcNuPsj5vXRghe1v\nRav+b0xx358KUQ/7bf+uIH5fh+dOBhZTC6fXLigXbMwb+0HQioTWi4LaZUwaoaO7\nz4Hsvt+DG/6K4olFlom0Gimt8wpSqVXGYz6ZUM6HA2PkcaCfOSKzDxJvcj23cef5\nOFt7GQKBgQDDIBeEq6UIFx3l27XBQqCESB7uFmGYqf8J2P8rGex2bGXtorgKxSEb\nRyugzdY8cHv4r7lszftRTfO6bFE709abGCbd5VMQcqWC2D/LjRh9cWD78Rl4vwft\nhQdirzpuW/UTtj0NfikdqDocPbBIC0F18LQIewK0vkkDTgFUzSnRrg==\n-----END RSA PRIVATE KEY-----'),

(21,'Manama','BH','2021-06-13 22:55:27.384371','Radius Client','bh-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.20.213','157.175.70.188','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:55:27.384371','-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEAhnIKJOZ5kNAFBjEUCZLUy/afSI2yqu7icCZmB7WLP/CWA6OM\nLnvWBFeMffuoEU6JrQUrF8kNvPykqqpq5Q3sWFYX938jlEhTyLckSQGIqb38CiD+\nrsl3UEp4ouqK9uKu1EROKK/pGrN9M+kQxyPnpk4ziqt8AAdQ/1YnOd7WqXsGoVil\nrfddcM0Me4zcf6liE7eBuq6K/bGwL1hCXiQ83yf5LWepI+0qYjJ3HYXEqmF6zIUN\nwJkwi6+I+d4zUYHtkqQudsbeOAv83Ghfh4RHex9Akp/NeLyhjUE9w66Ll0GaRJwd\nnfGKd2GzTwAgLMJoJkeWGUeSevnIXqHN+YL04wIDAQABAoIBAQCAgrlXlEZXOb/T\nr03r6MInRFH01B0YbsdbVBjq/YlhUOLUD5yLQivMcrMEKWz5Pg0zqWELl07eetAb\nolITKD/mIEVAefLUZl5P2V+BuUmcltE4DVnkUQcPZF/zuiWi4AMlOzVKhvtWXoMq\n/3jeIV/oA5AY2NnqXRP7Paq5Elw2GtPOno7yig7FeoGsbw2xEiqwJacbLl+aPVMG\n876INpMho58p3fC4TzlZy0yjUMyreVYp+JXstOht0A6Yu3XfMLDshecUsZ9z3hpx\nFOv0o/t7m+EzMOWZly7BWqPNQR/Z2KE/tzRJ6iq7OqgA63AOScOtNC2x8ErMbR1X\naUK2Wk6BAoGBAMB/P3uuMXBbaHc+nUNc4nwstevo+xL1TyDcHpqiTPc+gQWCn17F\nvOfo690ZgfKGfZfPfx1KlDuWCC/W5rldwnVmkFmzHRuIulQLfFGo0CwHxeszkC8+\n3x2yPJU9bqC85mD3WZir3KHHR6aWwQJ9/3ptV6yM0q0X4BgYmxJDG4kzAoGBALLM\nOKg0Z3zquUbclm/DRBAdkaHYvYKwJMgpAfUJJ5FgJ5Pv11W8XCi9jUHEeIilXH/m\nUsBMpswdhTtRUQb+bY61dPZxiUebb3toyDwwZx2PmRjkyCYNzwfh+d1wHOpVhics\nG4y0oDGFf14Pkirut5vhaUnR53K7IC/rgYjcUsWRAoGAZZZB0GmbWapDtTpsEBLC\ny7lkwH0Rk222Nqcz13Nznr9zo47NwFwmV1UKtIIzm+Px/93XPYQQOmnxJB4LYSPj\n7cUUxOHLqK49dwEguI+YXMQ1ZOyvWN+bK7bWVgQ3j8hg5CdlNzbrlszuOTIx5bjM\nzLVLoZonzIVfFBhYLgkQTRUCgYA0fN05C8FBm4pucf07rAAEFxoUgBMKFXmGxPsN\nGvXhqG+V7zewtI39a7/XCb00qA6igVPTneztSNE+251Ex1YUKdK/Mg9R1SCCAv2d\nJbxvoiMYerkC6tTXBJCcX/gtFpQ+VaV0xeWtA/qi+0E2l0ORYDfPcu5V8Pb++kzZ\nh4xI8QKBgEbKI+goUi8uRI3FcCtk/54qmSAat/f8x2WZpTmd1EPAMWPPaUhEH1Zu\nPgNsrO2CcHEaWIsUneLnypm/GHe5VybOdQLHGmq67Ou6cVheClEA/2AWpETRRYU+\nw5V3T/SQgZPBJOUH9KG+hW6LfYStSy+yeRGb9Z0EXs/JAYhiaOMC\n-----END RSA PRIVATE KEY-----'),

(22,'Tehran','IR','2021-06-14 17:02:08.470614','Radius Client','ir-cl.iraip.com','sudo occtl disconnect user {username}',22,'213.232.125.35','213.232.125.35','','Ay9W2QsUrhJ0bC63','GbnratfTXp1gxVxm/JDoAA==cX9UbjLfbWXaAWyJYxTy0Q==','root','other','2021-06-14 17:02:08.470614',NULL),

(23,'London','UK','2021-06-14 17:03:47.299063','Radius Client','uk-cl.iraip.com','vpn-sessiondb logoff {username}',22,'178.239.168.62','178.239.168.62','enable','Ay9W2QsUrhJ0bC63','','OrbVPN','cisco','2021-06-14 17:03:47.299063','');

insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (1, '18.230.148.147', 'br-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (2, '100.25.181.80', 'us-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (3, '3.131.119.158', 'usoh-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (4, '18.144.81.121', 'usca-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (5, '34.209.25.245', 'usor-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (6, '13.245.16.178', 'za-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (7, '18.162.225.96', 'hk-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (8, '13.233.79.2', 'in-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (9, '13.208.251.171', 'jp-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (10, '3.35.209.228', 'sk-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (11, '54.254.183.29', 'sg-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (12, '13.236.208.66', 'au-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (13, '52.194.225.49', 'jpto-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (14, '3.96.216.2', 'ca-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (15, '18.184.52.226', 'de-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (16, '34.244.240.144', 'ie-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (17, '18.133.184.96', 'uk.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (18, '15.161.46.161', 'it-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (19, '35.180.247.61', 'fr-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (20, '13.53.45.189', 'se-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (21, '157.175.70.188', 'bh-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (22, '213.232.125.35', 'ir-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (23, '178.239.168.62', 'uk-cl.iraip.com', 'cisco', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
