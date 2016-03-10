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
  `empid` <type>,
  `hourly_rate` <type>,
  `ssn` <type>,
  `hours_per_week` <type>,
  `date_hired` <type>,
  `date_terminated` <type>,
  `job_title` <type>
);

CREATE TABLE `Customer` (
  `cust_id` <type>,
  `reward_pts` <type>
);

CREATE TABLE `Menu_Item` (
  `name` <type>,
  `type` <type>,
  `price` <type>,
  `est_prep_time` <type>
);

CREATE TABLE `Person` (
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