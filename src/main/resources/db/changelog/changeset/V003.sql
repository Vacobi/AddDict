ALTER TABLE subscribe_dictionaries
    DROP CONSTRAINT IF EXISTS fk_subscribe_dictionaries_dictionary;

ALTER TABLE subscribe_dictionaries
    ADD CONSTRAINT fk_subscribe_dictionaries_dictionary
        FOREIGN KEY (dictionary_id)
            REFERENCES dictionaries(id)
            ON DELETE CASCADE;