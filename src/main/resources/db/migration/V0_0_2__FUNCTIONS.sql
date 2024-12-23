CREATE OR REPLACE FUNCTION calculate_total_number()
    RETURNS bigint AS $$
DECLARE
    total bigint;
BEGIN
    SELECT SUM(number) INTO total FROM Ticket;
    RETURN COALESCE(total, 0); -- Если в таблице нет строк, возвращаем 0
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION find_tickets_by_comment_substring(search_text varchar)
    RETURNS TABLE(
        id bigint,
        comment varchar,
        creation_date date,
        discount real,
        name varchar,
        number bigint,
        price double precision,
        type TicketType,
        coordinates_id bigint,
        person_id bigint,
        event_id bigint,
        venue_id bigint,
        owner_id bigint
    ) AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM Ticket
        WHERE comment ILIKE '%' || search_text || '%'; -- Поиск по подстроке с учетом регистра
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_tickets_by_comment_prefix(prefix_text varchar)
    RETURNS TABLE(
        id bigint,
        comment varchar,
        creation_date date,
        discount real,
        name varchar,
        number bigint,
        price double precision,
        type TicketType,
        coordinates_id bigint,
        person_id bigint,
        event_id bigint,
        venue_id bigint,
        owner_id bigint
    ) AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM Ticket
        WHERE comment ILIKE prefix_text || '%'; -- Поиск строк, начинающихся с подстроки
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION duplicate_ticket_as_vip(ticket_id bigint)
    RETURNS bigint AS $$
DECLARE
    new_ticket_id bigint;
BEGIN
    -- Вставляем новый билет, основанный на указанном
    INSERT INTO Ticket (comment, creation_date, discount, name, number,
                        price, type,
                        coordinates_id, person_id, event_id, venue_id, owner_id)
    SELECT
        comment, creation_date, discount, name, number,
        price * 2,    -- Удваиваем цену
        'VIP'::TicketType, -- Устанавливаем категорию как 'VIP'
        coordinates_id, person_id, event_id, venue_id, owner_id
    FROM Ticket
    WHERE id = ticket_id
    RETURNING id INTO new_ticket_id; -- Запоминаем новый ID

    -- Возвращаем ID нового билета
    RETURN new_ticket_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION cancel_event_and_delete_tickets(event_id bigint)
    RETURNS void AS $$
BEGIN
    -- Удалить все билеты, связанные с указанным событием (если нет on delete cascade)
    -- DELETE FROM Ticket
    -- WHERE event_id = event_id;

    -- Удаляем само событие
    DELETE FROM Event
    WHERE id = event_id;

    -- Сообщение в логи
    RAISE NOTICE 'Event % and its tickets have been successfully deleted.', event_id;
END;
$$ LANGUAGE plpgsql;
