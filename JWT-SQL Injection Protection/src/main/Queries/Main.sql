use ca6_db;

show tables;

SET FOREIGN_KEY_CHECKS = 0;
drop table if exists STUDENTS;
drop table if exists OFFERS;
drop table if exists PREREQUISITES;
drop table if exists OFFERDAYS;
drop table if exists GRADES;
drop table if exists TAKENCOURSES;
drop table if exists WAITINGLIST;
drop table if exists SELECTEDNOW;
SET FOREIGN_KEY_CHECKS = 1;


create table STUDENTS (
   studentId varchar(255) primary key,
   name tinytext not null,
   secondName tinytext not null,
   birthDate tinytext not null,
   field tinytext not null,
   faculty tinytext not null,
   level tinytext not null,
   status tinytext not null,
   img text not null,
   email text not null,
   password tinytext not null
);

-- describe STUDENTS;



create table OFFERS (
   code varchar(255),
   classCode varchar(255),
   name tinytext not null,
   units int default -1,
   type tinytext not null,
   instructor tinytext not null,
   capacity int default -1,
   numSignedUp int default -1,
   classTime tinytext not null,
   examTimeStart  tinytext not null,
   examTimeEnd  tinytext not null,
   primary key (code , classCode)
);

-- describe OFFERS;



create table PREREQUISITES (
   code varchar(255) not null,
   preCode varchar(255) not null,
   primary key (code , preCode)
);

-- describe PREREQUISITES;



create table OFFERDAYS (
   code varchar(255) not null,
   classCode varchar(255) not null,
   day varchar(255) not null,
   primary key (code , classCode , day),
   foreign key (code , classCode) references OFFERS(code , classCode)
);

-- describe OFFERDAYS;


create table GRADES (
   studentId varchar(255) not null,
   code varchar(255) not null,
   grade int(4) not null,
   term int(4) not null,
   primary key (studentId , code , grade , term),
   foreign key (studentId) references STUDENTS(studentId)
);

-- describe GRADES;


create table TAKENCOURSES (
   studentId varchar(255) not null,
   code varchar(255) not null,
   classCode varchar(255) not null,
   status varchar(255) default 'NOT_FINALIZED',
   primary key (studentId , code , classCode),
   foreign key (studentId) references STUDENTS(studentId),
   foreign key (code , classCode) references OFFERS(code , classCode)
);

-- describe TAKENCOURSES;

create table WAITINGLIST (
   code varchar(255) not null,
   classCode varchar(255) not null,
   studentId varchar(255) not null,
   primary key (code , classCode , studentId),
   foreign key (studentId) references STUDENTS(studentId),
   foreign key (code , classCode) references OFFERS(code , classCode)
);

-- describe WAITINGLIST;


create table SELECTEDNOW (
   studentId varchar(255) not null,
   code varchar(255) not null,
   classCode varchar(255) not null,
   status varchar(255) default 'NOT_FINALIZED',
   primary key (studentId , code , classCode),
   foreign key (studentId) references STUDENTS(studentId),
   foreign key (code , classCode) references OFFERS(code , classCode)
);

-- describe SELECTEDNOW;


select * from STUDENTS;

select * from OFFERS;

select * from PREREQUISITES;

select * from OFFERDAYS;

select * from GRADES;

select * from TAKENCOURSES;

select * from WAITINGLIST;

select * from SELECTEDNOW;