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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

/**
 * ResultSetIteratorTest
 */
public class ResultSetIteratorTest extends BaseTestCase {

    @Test
    public void testCreatesResultSetIteratorTakingThreeArgumentsAndCallsRemove() {

    	 final ResultSet resultSet = mock(ResultSet.class);
         final ResultSetIterator resultSetIterator = new ResultSetIterator(resultSet, null );
         resultSetIterator.remove();

    }

    public void testNext() {

        final Iterator<Object[]> iter = new ResultSetIterator(this.getResultSet());

        assertTrue(iter.hasNext());
        Object[] row = iter.next();
        assertEquals(COLS, row.length);
        assertEquals("1", row[0]);
        assertEquals("2", row[1]);
        assertEquals("THREE", row[2]);

        assertTrue(iter.hasNext());
        row = iter.next();
        assertEquals(COLS, row.length);

        assertEquals("4", row[0]);
        assertEquals("5", row[1]);
        assertEquals("SIX", row[2]);

        assertFalse(iter.hasNext());
    }

    @Test
    public void testRethrowThrowsRuntimeException() {

        final ResultSetIterator resultSetIterator = new ResultSetIterator((ResultSet) null);
        final Throwable throwable = new Throwable();
        final SQLException sQLException = new SQLException(throwable);

        try {
            resultSetIterator.rethrow(sQLException);
            fail("Expecting exception: RuntimeException");
        } catch (final RuntimeException e) {
            assertEquals(ResultSetIterator.class.getName(), e.getStackTrace()[0].getClassName());
        }

    }
    

    @Test
    public void testNextAsync() throws ExecutionException, InterruptedException, SQLException {
        // Create a ResultSetIterator with the mock ResultSet and executor service
    	ResultSet resultSet = mock(ResultSet.class);
    	RowProcessor convert = mock(BasicRowProcessor.class);
          // Mock behavior of RowProcessor.toArray() to return a dummy array
          when(convert.toArray(any(ResultSet.class))).thenReturn(new Object[] { "dummy", "data" });
    	when(resultSet.next()).thenReturn(true).thenReturn(false);
    	ExecutorService executorService = Executors.newSingleThreadExecutor();
        ResultSetIterator resultSetIterator = new ResultSetIterator(resultSet,convert, executorService);

        // Execute nextAsync
        CompletableFuture<Object[]> future = resultSetIterator.nextAsync();

        // Wait for the CompletableFuture to complete
        Object[] result = future.get();

        // Assert that the result is not null
        assertNotNull(result);

        // Add more assertions as needed based on the expected behavior of nextAsync
    }
    
}
