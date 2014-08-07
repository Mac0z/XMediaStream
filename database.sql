DROP TABLE IF EXISTS
static_music_file,
static_music_genre,
static_music_artist,
static_music_album,
static_music_tags,
static_video_file,
static_video_genre,
system_static_users,
system_dynamic_authenticated_users,
dynamic_xbmsp_incoming_request,
dynamic_xbmsp_outgoing_reply,
dynamic_shoutcast_buffer,
dynamic_update_queue,
dynamic_flow_control,
dynamic_bluetooth_users,
dynamic_upnp_instances,
dynamic_twitter_raw_data,
dynamic_twitter_hashtags;

CREATE TABLE dynamic_update_queue (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
internal_update_time BIGINT(13) NOT NULL,
table_name VARCHAR(50) NOT NULL,
updated_table_record_number BIGINT(13) NOT NULL,
update_action VARCHAR(1) NOT NULL,
INDEX USING BTREE(internal_update_time))
TYPE=HEAP;

INSERT INTO dynamic_update_queue (internal_update_time, table_name, updated_table_record_number, update_action)
VALUES (0, 'DUMMY', 0, 'A');

CREATE TABLE static_music_file (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
music_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
track_number INT(3),
album_id INT(11),
artist_id INT(11),
genre_id INT(11),
track_name VARCHAR(50),
file_location VARCHAR(500),
bit_rate INT(4),
PRIMARY KEY(record_number), INDEX(music_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_music_file_insert AFTER INSERT on static_music_file
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_file', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_music_file_update AFTER UPDATE on static_music_file
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_file', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_music_file_delete AFTER DELETE on static_music_file
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_file', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE static_music_genre (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
genre_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
genre_name VARCHAR(50),
genre_description VARCHAR(500),
PRIMARY KEY(record_number), INDEX(genre_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_music_genre_insert AFTER INSERT on static_music_genre
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_genre', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_music_genre_update AFTER UPDATE on static_music_genre
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_genre', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_music_genre_delete AFTER DELETE on static_music_genre
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_genre', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE static_music_artist (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
artist_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
artist_name VARCHAR(50),
PRIMARY KEY(record_number), INDEX(artist_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_music_artist_insert AFTER INSERT on static_music_artist
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_artist', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_music_artist_update AFTER UPDATE on static_music_artist
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_artist', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_music_artist_delete AFTER DELETE on static_music_artist
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_artist', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE static_music_album (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
album_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
album_name VARCHAR(50),
PRIMARY KEY(record_number), INDEX(album_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_music_album_insert AFTER INSERT on static_music_album
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_album', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_music_album_update AFTER UPDATE on static_music_album
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_album', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_music_album_delete AFTER DELETE on static_music_album
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_album', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE static_music_tags (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
music_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
tag VARCHAR(50),
PRIMARY KEY(record_number))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_music_tags_insert AFTER INSERT on static_music_tags
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_tags', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_music_tags_update AFTER UPDATE on static_music_tags
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_tags', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_music_tags_delete AFTER DELETE on static_music_tags
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_music_tags', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE static_video_file (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
video_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
viedo_genre INT(11),
video_name VARCHAR(50),
file_location VARCHAR(500),
PRIMARY KEY(record_number), INDEX(video_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_video_file_insert AFTER INSERT on static_video_file
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_video_file', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_video_file_update AFTER UPDATE on static_video_file
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_video_file', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_video_file_delete AFTER DELETE on static_video_file
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_video_file', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE static_video_genre (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
genre_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
genre_name VARCHAR(50),
PRIMARY KEY(record_number), INDEX(genre_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER static_video_genre_insert AFTER INSERT on static_video_genre
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_video_genre', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER static_video_genre_update AFTER UPDATE on static_video_genre
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_video_genre', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER static_video_genre_delete AFTER DELETE on static_video_genre
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'static_video_genre', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE system_static_users (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
user_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
user_name VARCHAR(50) NOT NULL,
user_password VARCHAR(50) NOT NULL,
bluetooth_id VARCHAR(17),
PRIMARY KEY(record_number), INDEX(user_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER system_static_users_insert AFTER INSERT on system_static_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'system_static_users', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER system_static_users_update AFTER UPDATE on system_static_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'system_static_users', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER system_static_users_delete AFTER DELETE on system_static_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'system_static_users', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE system_dynamic_authenticated_users (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
user_id INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
PRIMARY KEY(record_number), INDEX(user_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER system_dynamic_authenticated_users_insert AFTER INSERT on system_dynamic_authenticated_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'system_dynamic_authenticated_users', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER system_dynamic_authenticated_users_update AFTER update on system_dynamic_authenticated_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'system_dynamic_authenticated_users', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER system_dynamic_authenticated_users_delete AFTER DELETE on system_dynamic_authenticated_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'system_dynamic_authenticated_users', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_xbmsp_incoming_request (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
connection_handler INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
sequence_number INT(11) NOT NULL,
packet_number INT(11) NOT NULL,
raw_in_packet BLOB(1024),
PRIMARY KEY(record_number), INDEX(connection_handler,sequence_number,packet_number))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER dynamic_xbmsp_incoming_request_insert AFTER INSERT on dynamic_xbmsp_incoming_request
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_xbmsp_incoming_request', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_xbmsp_incoming_request_update AFTER UPDATE on dynamic_xbmsp_incoming_request
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_xbmsp_incoming_request', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_xbmsp_incoming_request_delete AFTER DELETE on dynamic_xbmsp_incoming_request
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_xbmsp_incoming_request', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_xbmsp_outgoing_reply (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
connection_handler INT(11) NOT NULL,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
sequence_number INT(11) NOT NULL,
packet_number INT(11) NOT NULL,
raw_out_packet BLOB(1024),
PRIMARY KEY(record_number), INDEX(connection_handler,sequence_number,packet_number))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER dynamic_xbmsp_outgoing_reply_insert AFTER INSERT on dynamic_xbmsp_outgoing_reply
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_xbmsp_outgoing_reply', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_xbmsp_outgoing_reply_update AFTER UPDATE on dynamic_xbmsp_outgoing_reply
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_xbmsp_outgoing_reply', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_xbmsp_outgoing_reply_delete AFTER DELETE on dynamic_xbmsp_outgoing_reply
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_xbmsp_outgoing_reply', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_shoutcast_buffer (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
music_id INT(11) NOT NULL,
source_name VARCHAR(12) NOT NULL,
raw_data BLOB,
PRIMARY KEY(record_number), INDEX(update_time), INDEX(record_number, source_name))
TYPE=InnoDB;

DELIMITER |
CREATE TRIGGER dynamic_shoutcast_buffer_insert AFTER INSERT on dynamic_shoutcast_buffer
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_shoutcast_buffer', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_shoutcast_buffer_update AFTER UPDATE on dynamic_shoutcast_buffer
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_shoutcast_buffer', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_shoutcast_buffer_delete AFTER DELETE on dynamic_shoutcast_buffer
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_shoutcast_buffer', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_flow_control (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
control_name VARCHAR(50),
thread VARCHAR(50),
table_name VARCHAR(50),
last_read_time TIMESTAMP,
PRIMARY KEY(record_number), INDEX(control_name, thread, table_name))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER dynamic_flow_control_insert AFTER INSERT on dynamic_flow_control
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_flow_control', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_flow_control_update AFTER UPDATE on dynamic_flow_control
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_flow_control', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_flow_control_delete AFTER DELETE on dynamic_flow_control
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_flow_control', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_bluetooth_users (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
user_id INT(11),
user_string VARCHAR(50) NOT NULL,
bluetooth_id VARCHAR(17),
PRIMARY KEY(record_number), INDEX(user_id, bluetooth_id))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER dynamic_bluetooth_users_insert AFTER INSERT on dynamic_bluetooth_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_bluetooth_users', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_bluetooth_users_update AFTER UPDATE on dynamic_bluetooth_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_bluetooth_users', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_bluetooth_users_delete AFTER DELETE on dynamic_bluetooth_users
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_bluetooth_users', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_upnp_instances (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
host VARCHAR(32),
location VARCHAR(128),
nt VARCHAR(128),
usn VARCHAR(1024),
bootid INT(11),
config_id INT(11),
search_port INT(5),
expire_time TIMESTAMP,
PRIMARY KEY(record_number), INDEX(usn))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER dynamic_upnp_instances_insert AFTER INSERT on dynamic_upnp_instances
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_upnp_instances', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_upnp_instances_update AFTER UPDATE on dynamic_upnp_instances
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_upnp_instances', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_upnp_instances_delete AFTER DELETE on dynamic_upnp_instances
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_upnp_instances', OLD.record_number, 'D');
END;
|

DELIMITER ;

CREATE TABLE dynamic_twitter_raw_data (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
data TEXT,
PRIMARY KEY(record_number))
TYPE=MYISAM;

DELIMITER |
CREATE TRIGGER dynamic_twitter_raw_data_insert AFTER INSERT on dynamic_twitter_raw_data
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_twitter_raw_data', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_twitter_raw_data_update AFTER UPDATE on dynamic_twitter_raw_data
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_twitter_raw_data', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_twitter_raw_data_delete AFTER DELETE on dynamic_twitter_raw_data
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_twitter_raw_data', OLD.record_number, 'D');
END;
|

CREATE TABLE dynamic_twitter_hashtags (
update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
record_number BIGINT(13) AUTO_INCREMENT NOT NULL,
hashtag VARCHAR(140),
tweet_time Timestamp,
PRIMARY KEY(record_number))
TYPE=MYISAM;

DELIMITER ;

DELIMITER |
CREATE TRIGGER dynamic_twitter_hashtags_insert AFTER INSERT on dynamic_twitter_hashtags
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_twitter_hashtags', NEW.record_number, 'A');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_twitter_hashtags_update AFTER UPDATE on dynamic_twitter_hashtags
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_twitter_hashtags', NEW.record_number, 'M');
END;
|

DELIMITER |
CREATE TRIGGER dynamic_twitter_hashtags_delete AFTER DELETE on dynamic_twitter_hashtags
FOR EACH ROW BEGIN
SET @updateId := (SELECT MAX(internal_update_time) from dynamic_update_queue);
INSERT INTO dynamic_update_queue (internal_update_time, table_name, 
updated_table_record_number, update_action)
VALUES ((SELECT @updateId:=@updateId+1), 'dynamic_twitter_hashtags', OLD.record_number, 'D');
END;
|

DELIMITER ;

INSERT INTO system_static_users
(user_id, user_name, user_password, bluetooth_id)
VALUES (1, "guest", "2ec099f2d602cc4968c5267970be1326", "00:00:00:00:00:00");

INSERT INTO system_static_users
(user_id, user_name, user_password, bluetooth_id)
VALUES (2, "oliver", "0459f77c53c6605be7654ac4ac4fed2b", "00:21:06:7B:05:F4");

