# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table asistencia (
  id                            integer auto_increment not null,
  empleado_id                   bigint,
  capacitacion_id               bigint,
  horas                         double,
  constraint pk_asistencia primary key (id)
);

create table capacitacion (
  id                            bigint auto_increment not null,
  descripcion                   varchar(255),
  fecha_inicio                  timestamp,
  fecha_final                   timestamp,
  area                          varchar(255),
  lugar                         varchar(255),
  facilitador                   varchar(255),
  observaciones                 varchar(255),
  costo                         double,
  constraint pk_capacitacion primary key (id)
);

create table empleado (
  id                            bigint auto_increment not null,
  nombre                        varchar(255),
  cedula                        integer,
  activo                        boolean,
  constraint uq_empleado_cedula unique (cedula),
  constraint pk_empleado primary key (id)
);

alter table asistencia add constraint fk_asistencia_empleado_id foreign key (empleado_id) references empleado (id) on delete restrict on update restrict;
create index ix_asistencia_empleado_id on asistencia (empleado_id);

alter table asistencia add constraint fk_asistencia_capacitacion_id foreign key (capacitacion_id) references capacitacion (id) on delete restrict on update restrict;
create index ix_asistencia_capacitacion_id on asistencia (capacitacion_id);


# --- !Downs

alter table asistencia drop constraint if exists fk_asistencia_empleado_id;
drop index if exists ix_asistencia_empleado_id;

alter table asistencia drop constraint if exists fk_asistencia_capacitacion_id;
drop index if exists ix_asistencia_capacitacion_id;

drop table if exists asistencia;

drop table if exists capacitacion;

drop table if exists empleado;

