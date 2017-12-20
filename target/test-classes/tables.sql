use cs1433545;
DROP TABLE IF EXISTS jagAttachment;
DROP TABLE IF EXISTS jagFolder;
DROP TABLE IF EXISTS jagEmail;
CREATE TABLE jagEmail (
  emailId int(11) PRIMARY KEY AUTO_INCREMENT,
  senderEmail varchar(255) DEFAULT NULL,
  receiverEmail varchar(255) DEFAULT NULL,
  emailSubject varchar(255) DEFAULT NULL,
  emailText varchar(255) DEFAULT NULL,
  emailHtml varchar(255) DEFAULT NULL,
  cc varchar(255) DEFAULT NULL,
  bcc varchar(255) DEFAULT NULL,
  folderName varchar(255) NOT NULL,
  mailDate timestamp  default current_timestamp 
  
);
CREATE TABLE jagFolder (
  folderId int(11) PRIMARY KEY AUTO_INCREMENT,
  folderName varchar(255) NOT NULL,
  userEmail varchar(255) NOT NULL
 
  
);
CREATE TABLE jagAttachment (
  attachmentId int(11) PRIMARY KEY AUTO_INCREMENT,
  attachmentName varchar(255) DEFAULT NULL,
  attachmentContent mediumblob NULL,
  emailId int(11) NOT NULL,
  
  FOREIGN KEY (emailId) REFERENCES jagEmail(emailId) ON DELETE CASCADE
 
  
);
INSERT INTO jagFolder(folderName, userEmail) values ('Sent', 'dabaws777@gmail.com');
INSERT INTO jagFolder(folderName, userEmail) values ('Trash', 'dabaws777@gmail.com');
INSERT INTO jagFolder(folderName, userEmail) values ('Spam', 'dabaws777@gmail.com');
INSERT INTO jagEmail(senderEmail,receiverEmail,emailSubject,emailText, emailHtml, cc,bcc,folderName,mailDate) 
values ('dabaws777@gmail.com', 'nasjoseph7@gmail.com','subject', 'hehe','<h1>yo</h1>','williamngoreceive@hotmail.com',
'williamngoreceive@hotmail.com','Sent', null);
