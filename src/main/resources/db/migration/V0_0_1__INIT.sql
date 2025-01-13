create type UserType as enum ('ADMIN', 'MEMBER');

create table Users
(
    id                      bigserial        primary key,
    credentials_non_expired boolean          not null,
    enabled                 boolean          not null,
    login                   varchar(255)     not null unique,
    non_expired             boolean          not null,
    non_locked              boolean          not null,
    pass                    varchar(255)     not null,
    user_type               UserType         not null
);

create type AdminRequestStatus as enum ('PENDING', 'ACCEPTED', 'REJECTED');

create table Admin_Request
(
    id                      bigserial        primary key,
    user_id                 bigint,
    request_date            timestamp,
    status                  AdminRequestStatus,
    comment                 text,
    foreign key (user_id) REFERENCES Users (id) ON DELETE cascade ON UPDATE cascade
);

create table Location
(
    id bigserial        primary key,
    x  integer          not null,
    y  bigint           not null,
    z  double precision not null
);

create type Color as enum ('BLUE', 'BROWN', 'WHITE');

create table Person
(
    id          bigserial        primary key,
    birthday    date             not null,
    eye_color   Color            not null,
    hair_color  Color            not null,
    height      double precision check (height >= (1)::double precision),
    weight      integer          check (weight >= 1),
    location_id bigint           not null,
    foreign key (location_id) REFERENCES Location (id) ON DELETE cascade ON UPDATE cascade
);

create table Event
(
    id            bigserial    primary key,
    min_age       bigint       not null,
    name          varchar(255) not null,
    tickets_count integer      not null check (tickets_count >= 1),
    owner_id      bigint       not null,
    foreign key (owner_id) REFERENCES Users (id) ON DELETE cascade ON UPDATE cascade
);

create type VenueType as enum ('CINEMA', 'LOFT', 'MALL', 'STADIUM', 'THEATRE');

create table Venue
(
    id       bigserial         primary key,
    capacity integer           not null check (capacity >= 1),
    name     varchar(255)      not null,
    type     VenueType         not null,
    owner_id bigint            not null,
    foreign key (owner_id) REFERENCES Users (id) ON DELETE cascade ON UPDATE cascade
);

create table Coordinates
(
    id bigserial primary key,
    x  integer   not null,
    y  integer   not null
);

create type TicketType as enum ('BUDGETARY', 'USUAL', 'VIP');

create table Ticket
(
    id             bigserial          primary key,
    comment        varchar(426),
    creation_date  date               not null,
    discount       real               not null check ((discount <= (99)::double precision) AND (discount >= (1)::double precision)),
    name           varchar(255)       not null,
    number         bigint             not null check (number >= 1),
    price          double precision   check (price >= (1)::double precision),

    type           Tickettype         not null,

    coordinates_id bigint             not null unique,
    foreign key (coordinates_id) REFERENCES Coordinates (id) ON DELETE cascade ON UPDATE cascade,
    person_id      bigint             not null unique,
    foreign key (person_id) REFERENCES Person (id) ON DELETE cascade ON UPDATE cascade,
    event_id       bigint,
    foreign key (event_id) REFERENCES Event (id) ON DELETE cascade ON UPDATE cascade,
    venue_id       bigint,
    foreign key (venue_id) REFERENCES Venue (id) ON DELETE cascade ON UPDATE cascade,
    owner_id       bigint             not null,
    foreign key (owner_id) REFERENCES Users (id) ON DELETE cascade ON UPDATE cascade
);
