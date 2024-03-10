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
package org.apache.commons.dbutils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides generous name matching (e.g. underscore-aware) from DB
 * columns to Java Bean properties.
 *
 * @since 1.6
 */
public class GenerousBeanProcessor extends BeanProcessor {
	
	private static final Logger logger = LogManager.getLogger(GenerousBeanProcessor.class);

    /**
     * Default constructor.
     */
    public GenerousBeanProcessor() {
    }

	/*
	 * mapping columns from a ResultSet to properties defined by a set of
	 * PropertyDescriptors. However, the second code snippet utilizes Java 8's
	 * Stream API to accomplish the same task in a more concise and expressive
	 * manner.
	 */    @Override
    protected int[] mapColumnsToProperties(final ResultSetMetaData rsmd,
            final PropertyDescriptor[] props) throws SQLException {

        final int cols = rsmd.getColumnCount();
        final int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

//        for (int col = 1; col <= cols; col++) {
//            String columnName = rsmd.getColumnLabel(col);
//
//            if (null == columnName || 0 == columnName.length()) {
//                columnName = rsmd.getColumnName(col);
//            }
//
//            final String generousColumnName = columnName
//                    .replace("_", "")   // more idiomatic to Java
//                    .replace(" ", "");  // can't have spaces in property names
//
//            for (int i = 0; i < props.length; i++) {
//                final String propName = props[i].getName();
//
//                // see if either the column name, or the generous one matches
//                if (columnName.equalsIgnoreCase(propName) ||
//                        generousColumnName.equalsIgnoreCase(propName)) {
//                    columnToProperty[col] = i;
//                    break;
//                }
//            }
//        }
        
        IntStream.rangeClosed(1, cols)
        .forEach(col -> {
            try {
                String columnName = rsmd.getColumnLabel(col);
                if (null == columnName || 0 == columnName.length()) {
                    columnName = rsmd.getColumnName(col);
                }
                final String finalColumnName = columnName;
                final String generousColumnName = finalColumnName
                        .replace("_", "")   // more idiomatic to Java
                        .replace(" ", "");  // can't have spaces in property names

                IntStream.range(0, props.length)
                        .filter(i -> {
                            final String propName = props[i].getName();
                            // see if either the column name, or the generous one matches
                            return finalColumnName.equalsIgnoreCase(propName) ||
                                    generousColumnName.equalsIgnoreCase(propName);
                        })
                        .findFirst()
                        .ifPresent(i -> columnToProperty[col] = i);
            } catch (SQLException e) {
                // Handle SQLException
                logger.info("Exception message" + e.getMessage());
            }
        });

        return columnToProperty;
    }

}
