package de.jdynameta.base.creation.db;

import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.value.JdyPersistentException;

public interface JdbcSchemaHandler
  {

    public boolean existsSchema(String aSchemaName) throws JdyPersistentException;

    public void deleteSchema(String aSchemaName) throws JdyPersistentException;

    public void createSchema(String aSchemaName, ClassRepository aRepository)
            throws JdyPersistentException;

    public void validateSchema(String aSchemaName, ClassRepository aRepository)
            throws JdyPersistentException, SchemaValidationException;

    @SuppressWarnings("serial")
    public static class SchemaValidationException extends Exception
      {

        public SchemaValidationException()
        {
            super();
        }

        public SchemaValidationException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public SchemaValidationException(String message)
        {
            super(message);
        }

        public SchemaValidationException(Throwable cause)
        {
            super(cause);
        }

      }
  }
