/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@code ResultSetHandler} implementation that converts one
 * {@code ResultSet} column into an Object. This class is thread safe.
 *
 * @param <T> The type of the scalar
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class ScalarHandler<T> implements ResultSetHandler<T> {
	
	private static final Logger logger = LogManager.getLogger(ScalarHandler.class);

    /**
     * The column number to retrieve.
     */
    private final int columnIndex;

    /**
     * The column name to retrieve.  Either columnName or columnIndex
     * will be used but never both.
     */
    private final String columnName;

    /**
     * Creates a new instance of ScalarHandler.  The first column will
     * be returned from {@code handle()}.
     */
    public ScalarHandler() {
        this(1, null);
    }

    /**
     * Creates a new instance of ScalarHandler.
     *
     * @param columnIndex The index of the column to retrieve from the
     * {@code ResultSet}.
     */
    public ScalarHandler(final int columnIndex) {
        this(columnIndex, null);
    }

    /** Helper constructor
     * @param columnIndex The index of the column to retrieve from the
     * {@code ResultSet}.
     * @param columnName The name of the column to retrieve from the
     * {@code ResultSet}.
     */
    private ScalarHandler(final int columnIndex, final String columnName) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * Creates a new instance of ScalarHandler.
     *
     * @param columnName The name of the column to retrieve from the
     * {@code ResultSet}.
     */
    public ScalarHandler(final String columnName) {
        this(1, columnName);
    }

    /**
     * Returns one {@code ResultSet} column as an object via the
     * {@code ResultSet.getObject()} method that performs type
     * conversions.
     * @param resultSet {@code ResultSet} to process.
     * @return The column or {@code null} if there are no rows in
     * the {@code ResultSet}.
     *
     * @throws SQLException if a database access error occurs
     * @throws ClassCastException if the class datatype does not match the column type
     *
     * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
     */
    // We assume that the user has picked the correct type to match the column
    // so getObject will return the appropriate type and the cast will succeed.
    @SuppressWarnings("unchecked")
    @Override
    public T handle(final ResultSet resultSet) throws SQLException {

        if (resultSet.next()) {
            if (this.columnName == null) {
            	logger.info("Error message" +(T) resultSet.getObject(this.columnIndex));
                return (T) resultSet.getObject(this.columnIndex);
            }
            logger.info("Error message" +(T) resultSet.getObject(this.columnName));
            return (T) resultSet.getObject(this.columnName);
        }
        return null;
    }
}
