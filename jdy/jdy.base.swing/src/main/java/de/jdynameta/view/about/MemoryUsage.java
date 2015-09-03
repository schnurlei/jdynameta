/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.view.about;


public class MemoryUsage 
{
	private static final int RUNNABLE_SLEEP_TIME = 500;
	private volatile long maxUsageInBytes = 0;
	boolean isRunning = true;
	private long startTime;
	
	public void start(String[] args) throws Exception 
	{
		startTime = System.currentTimeMillis();
		Thread memory = new Thread(createMemoryUsageRunnable());
		memory.start();
	}


	public void endRun() 
	{
		isRunning = false;
	}

	public long getMaxUsage() 
	{
		return maxUsageInBytes;
	}
	
	public long getTimeRunningInMs()
	{
		long end = System.currentTimeMillis();
		return end - startTime;
	}
	
	private void checkMemUsage() 
	{
		Runtime rt = Runtime.getRuntime();
		long used = rt.totalMemory() - rt.freeMemory();
		if (used > maxUsageInBytes) {
			maxUsageInBytes = used;
		}
	}
	
	
	private Runnable createMemoryUsageRunnable()  
	{
		return new Runnable()
		{
			public void run() {
				while (isRunning) {
					checkMemUsage();
					try {
						Thread.sleep(RUNNABLE_SLEEP_TIME);
					} catch (InterruptedException e) {
						// IGNORE
					}
				}
			}
		};

	}


}

