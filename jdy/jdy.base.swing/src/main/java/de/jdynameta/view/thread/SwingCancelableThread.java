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
package de.jdynameta.view.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * thread which starts an runnable and which can be canceled from outside
  *
 */
public class SwingCancelableThread 
{
	private static ThreadGroup defaultThreadGroup = new ThreadGroup("SwingActionThreads");
	
	private Thread actionThread;
	private String description;
	private String currentStep;
	private Throwable exceptionInRun;
	private ThreadState state;
	private ArrayList<SwingActionThreadListener> listenerList;

	private SwingCancelableRunnable runnableToStart;
	private DefaultProgressModel progressModel;
	
	
	public SwingCancelableThread( String aDescription, SwingCancelableRunnable aRunnableToStart ) {
		
		this.description = aDescription;
		this.runnableToStart = aRunnableToStart;
		this.progressModel = new DefaultProgressModel(this);
		this.currentStep = "Waiting";
		setState( ThreadState.CREATED );
	}

	
	public final void start() 
	{
		// start thread only once, check whether cancel was called before start
		if( this.actionThread == null && !progressModel.isCanceled() )  {
			this.actionThread = new Thread(defaultThreadGroup, this.description)
			{
				@Override
				public void run() {
					setState( ThreadState.RUNNING );
					try {
						SwingCancelableThread.this.runnableToStart.run(progressModel);
					} catch (Throwable anException) {
						setExceptionInRun(anException);
						setState( ThreadState.FINISHED);
					}
					setState( ThreadState.FINISHED);
				}
			};
			this.actionThread.setUncaughtExceptionHandler(createUncaughtExceptionHandler());
			
			
			this.actionThread.start();
		}
	}


	private UncaughtExceptionHandler createUncaughtExceptionHandler() {

		return new Thread.UncaughtExceptionHandler()
			{

				public void uncaughtException(Thread aThread, Throwable anException) {

					setExceptionInRun(anException);
					setState( ThreadState.FINISHED);
				}
		
			};
	}
	
 	
	public final void cancel()
	{
		progressModel.setCanceled(true);
		if( this.actionThread != null) { 
			this.actionThread.interrupt();
		}
	}
 
	protected boolean isCanceled() 
	{
		return progressModel.isCanceled();
	}

	
	public Throwable getExceptionInRun() 
	{
		return exceptionInRun;
	}


	protected void setExceptionInRun(Throwable exceptionInRun) 
	{
		this.exceptionInRun = exceptionInRun;
	}


	public boolean hasFinished() 
	{
		return getState().equals(ThreadState.FINISHED);
	}
	
	private ThreadState getState() {
		return state;
	}


	private void setState(ThreadState state) {
		this.state = state;
		this.fireSwingActionThreadChanged();
	}

	public String getCurrentStep() 
	{
		return currentStep;
	}

	private void setCurrentStep(String currentStep) 
	{
		this.currentStep = currentStep;
		this.fireSwingActionThreadChanged();
		
	}

	public synchronized void addListener(SwingActionThreadListener aListener)
	{
		if( this.listenerList == null) {
			this.listenerList = new ArrayList<SwingActionThreadListener>();
		}
		
		this.listenerList.add(aListener);
	}

	public synchronized void removeListener(SwingActionThreadListener aListener)
	{
		if( this.listenerList != null) {
			this.listenerList.remove(aListener);
		}		
	}
	
	private void fireSwingActionThreadChanged()
	{
		if( this.listenerList != null) {
			SwingActionThreadEvent newEvent = new SwingActionThreadEvent(this);

			for ( SwingActionThreadListener curListener : this.listenerList ) {
				curListener.threadChanged(newEvent);
			}
		}
	}
	
	public enum ThreadState {
		RUNNING
		, FINISHED
		, CREATED
	}


	public static interface SwingActionThreadListener extends EventListener 
	{
		public void threadChanged(SwingActionThreadEvent aEvent); 
	}
	
	@SuppressWarnings("serial")
	public static class SwingActionThreadEvent extends EventObject
	{

		public SwingActionThreadEvent(Object aSource)
		{
			super(aSource); 
		}

	}
		
	private static class DefaultProgressModel implements SwingCancelableRunnable.CancelableProgressModel
	{
		private boolean isCanceled;
		private SwingCancelableThread actionThread;
		
		public DefaultProgressModel(SwingCancelableThread aActionThread) {
			this.isCanceled = false;
			this.actionThread = aActionThread;
		}
		
		@Override
		public synchronized boolean isCanceled() 
		{
			return isCanceled;
		}
		
		public synchronized void setCanceled(boolean isCanceled) {
			this.isCanceled = isCanceled;
		}	
		
		@Override
		public void setCurrentStep(String aStep) {
			
			this.actionThread.setCurrentStep(aStep);
		}
		
	}
}
