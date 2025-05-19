ALTER TABLE translations
    DROP CONSTRAINT IF EXISTS fk_translations_dictionaries;

ALTER TABLE translations
    ADD CONSTRAINT fk_translations_dictionaries
        FOREIGN KEY (dictionary_id)
            REFERENCES dictionaries(id)
            ON DELETE CASCADE;