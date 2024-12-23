create type ActionType as enum ('INSERT', 'UPDATE', 'DELETE');

CREATE TABLE Audit_Log
(
    id            bigserial      PRIMARY KEY,
    table_name    varchar(255)   NOT NULL,       -- Название таблицы
    record_id     bigint         NOT NULL,       -- ID записи в таблице
    action        ActionType     NOT NULL,
    changed_data  jsonb,                         -- Данные, которые были изменены
    user_id       bigint         NOT NULL REFERENCES users (id) ON DELETE SET NULL,
    action_time   timestamp      NOT NULL DEFAULT now()
);


CREATE OR REPLACE FUNCTION audit_ticket_changes()
    RETURNS TRIGGER AS $$
BEGIN
    -- Логирование действия INSERT
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, record_id, action, changed_data, user_id)
        VALUES ('ticket', NEW.id, 'INSERT'::actiontype, row_to_json(NEW), NEW.created_by);
        RETURN NEW;
    END IF;

    -- Логирование действия UPDATE
    IF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, record_id, action, changed_data, user_id)
        VALUES ('ticket', NEW.id, 'UPDATE'::actiontype, row_to_json(NEW), NEW.updated_by);
        RETURN NEW;
    END IF;

    -- Логирование действия DELETE
    IF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, record_id, action, changed_data, user_id)
        VALUES ('ticket', OLD.id, 'DELETE'::actiontype, row_to_json(OLD), OLD.updated_by);
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ticket_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON ticket
    FOR EACH ROW
EXECUTE FUNCTION audit_ticket_changes();
