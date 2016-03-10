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

CREATE TABLE Order (
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

CREATE TABLE Menu_Item (
  `name` <type>,
  `type` <type>,
  `price` <type>,
  `est_prep_time` <type>
);

CREATE TABLE Person (
  `personid` <type>,
  `name` <type>,
  `    first` <type>,
  `    middle  ` <type>,
  `    last` <type>,
  `date_of_birth` <type>,
  `{ phone_number }` <type>,
  `{ email_addr }` <type>,
  `username` <type>,
  `password_hash` <type>,
  `password_salt` <type>,
  `home_address` <type>,
  `    street` <type>,
  `    city` <type>,
  `    state` <type>,
  `    zip` <type>,
  `age()` <type>
);