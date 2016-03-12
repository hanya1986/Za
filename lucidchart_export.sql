CREATE TABLE Order_Item (
  order_id IDENTITY not null,
  name VARCHAR(200),
  quantity INT,
  primary key (order_id),
  foreign key (name) references Menu_Item
);

CREATE TABLE Credit_Card (
  number char(16),
  sec_code char(3),
  primary key(number),
);

CREATE TABLE `Order` (
  `orderid` INT,
  `custid` INT NOT NULL,
  `empid_took_order` INT NOT NULL,
  `empid_prepared_order` INT NOT NULL,
  `empid_delivered_order` INT NOT NULL,
  `time_order_placed` DATETIME NOT NULL,
  `time_order_out` DATETIME NOT NULL,
  `time_order_delivered` DATETIME NOT NULL,
  `subtotal` DECIMAL(7,2) NOT NULL,
  `tax` DECIMAL(7,2) NOT NULL,
  `total` DECIMAL(8,2) NOT NULL,
  `tip` DECIMAL(7,2),
  PRIMARY KEY (`orderid`),
  FOREIGN KEY (`custid`) REFERNCES `Customer`.`cust_id`,
  FOREIGN KEY (`empid_took_order`) REFERENCES `Employee`.`empid`,
  FOREIGN KEY (`empid_prepared_order`) REFERENCES `Employee`.`empid`,
  FOREIGN KEY (`empid_delivered_order`) REFERENCES `Employee`.`empid`
);

CREATE TABLE `Employee` (
  `empid` varchar(255),
  `hourly_rate` float(2),
  `ssn` int,
  `hours_per_week` float(2),
  `date_hired` varchar(10),
  `date_terminated` varchar(10),
  `job_title` varchar(255),
  primary key (`empid`)
);

CREATE TABLE `Customer` (
  `cust_id` varchar(255),
  `reward_pts` int
);

CREATE TABLE `Menu_Item` (
  `name` VARCHAR(200),
  `type` INT NOT NULL,
  `price` DECIMAL(2,2) NOT NULL,
  `est_prep_time` INT,
  PRIMARY KEY (`name`)
);

CREATE TABLE IF NOT EXISTS Person (
  personid IDENTITY PRIMARY KEY,
  first_name VARCHAR(255),
  middle_name VARCHAR(255),
  last_name VARCHAR(255),
  date_of_birth DATE,
  username VARCHAR(255),
  password_hash VARCHAR(255),
  password_salt VARCHAR(255),
  street VARCHAR(255),
  city VARCHAR(255),
  state CHAR(2),
  zip VARCHAR(20),
  age() INT
);

CREATE TABLE IF NOT EXISTS PersonPhoneNumber(
	personid INT,
	phone_number VARCHAR(20),
	FOREIGN KEY (personid) REFERENCES Person(personid)
	);
	
CREATE TABLE IF NOT EXISTS PersonEmailAddress(
	personid INT,
	email_addr VARCHAR(255),
	FOREIGN KEY (personid) REFERENCES Person(personid)
	);

CREATE TABLE IF NOT EXISTS CustomerCard (
    personid INT,
    card_number CHAR(16),
    PRIMARY KEY (personid, card_number),
    FOREIGN KEY (card_number) REFERENCES Credit_Card(number)
);