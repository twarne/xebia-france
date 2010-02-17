import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.management.counters.CounterRepository;

/*
 * Copyright 2008-2009 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public abstract class CxfClientPounder {

    public static void main(String[] args) throws MalformedURLException, InterruptedException {

        // pound(10, 1000, "http://localhost:8080/jmx-demo/");
        pound(25,10, "http://localhost:8080/jmx-demo/cxf-client-ok");
        // pound(1, 10, "http://localhost:8080/jmx-demo/soap/client/successful-client.jsp");
        
        pound(25,500, "http://localhost:8080/jmx-demo/cxf-client-ok");
        //pound(5, 100, "http://localhost:8080/jmx-demo/soap/client/successful-client.jsp");
    }

    private static void pound(final int threadCount, final int invocationPerThreadCount, String urlAsString)
        throws MalformedURLException, InterruptedException {
        System.out.println(urlAsString);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final URL url = new URL(urlAsString);
        for (int i = 0; i < threadCount; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < invocationPerThreadCount; j++) {
                        try {
                            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                            int actualResponseCode = connection.getResponseCode();

                            byte[] buffer = new byte[500];
                            InputStream in = connection.getInputStream();
                            int lenght;
                            while ((lenght = in.read(buffer)) >= 0) {
                                // purge result
                            }

                            if (actualResponseCode == HttpURLConnection.HTTP_OK) {
                                StressTestUtils.incrementProgressBarSuccess();
                            } else {
                                StressTestUtils.incrementProgressBarFailure();
                            }
                        } catch (IOException e) {
                            StressTestUtils.incrementProgressBarFailure();
                            e.printStackTrace();
                        }
                    }
                }
            };
            executor.execute(runnable);
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
    }
}
