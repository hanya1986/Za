CREATE TABLE `Order_Item` (
  `order_id` <type>,
  `item_id` <type>,
  `quantity` <type>
);

CREATE TABLE `Credit_Card` (
  `number` <type>,
  `sec_code` <type>
);

CREATE TABLE `Order` (
  `orderid` <type>,
  `custid` <type>,
  `empid_took_order` <type>,
  `empid_prepared_order` <type>,
  `empid_delivered_order` <type>,
  `time_order_placed` <type>,
  `time_order_out` <type>,
  `time_order_delivered` <type>,
  `subtotal` <type>,
  `tax` <type>,
  `total` <type>,
  `tip` <type>
);

CREATE TABLE `Employee` (
  `empid` varchar(255),
  `hourly_rate` float(2),
  `ssn` int,
  `hours_per_week` float(2),
  `date_hired` varchar(10),
  `date_terminated` varchar(10),
  `job_title` varchar(255)
);

CREATE TABLE `Customer` (
  `cust_id` varchar(255),
  `reward_pts` int
);

CREATE TABLE `Menu_Item` (
  `name` <type>,
  `type` <type>,
  `price` <type>,
  `est_prep_time` <type>
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
	
CREATE TABLE IF NOT EXISTS PersonPhoneNumber(
	personid INT,
	email_addr VARCHAR(255),
	FOREIGN KEY (personid) REFERENCES Person(personid)
	);