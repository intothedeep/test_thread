package com.thread.basic;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadBasic {
	public static void main(String[] args) throws InterruptedException {
		System.out.format("- Main:start: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(),Thread.currentThread().getName());

		DaemonThread daemon = new DaemonThread();
		Thread th = new Thread(daemon);
		th.setDaemon(true);
		th.start();
		
		ThreadB threadB = new ThreadB();
		threadB.start();
		
		// 0이 출력? 그럼 어떻게 하면 되나?
		System.out.format("Thread sum = %d \n", threadB.getSum());
		System.out.format("- Main:MID1: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(),Thread.currentThread().getName());

		// join method를 사용하면 쓰레드가 끝날 때 까지 기다린 후 아래부분을 실
		threadB.join();
		System.out.format("Thread w/ join() sum = %d \n", threadB.getSum());
		System.out.format("- Main:MID2: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(),Thread.currentThread().getName());
		
		// synchronized thread with 메소드
		ThreadSyncWMethod thSyncWMethod = new ThreadSyncWMethod();
		thSyncWMethod.start();
		synchronized (thSyncWMethod) {
			System.out.println("th sync w Method가 완료 될 때까지 기다립니다.");
			thSyncWMethod.wait();
		}
		
		// synchronized thread with 객체
		ThreadSyncWObject thSyncWObject = new ThreadSyncWObject();
		thSyncWObject.start();
		synchronized (thSyncWObject) {
			System.out.println("th sync w Object가 완료 될 때까지 기다립니다.");
			thSyncWObject.wait();
		}
		
		// 순서없이 실
//		new ThreadB().start();
//		new ThreadB().start();
		
		// callable을 사용한 쓰레드
		ThreadRunnable r = new ThreadRunnable();
		Thread thRunnable = new Thread(r);
		thRunnable.join();
		thRunnable.start();
		
		// runnalbe을 사용한 쓰레드
		ThreadCallable c = new ThreadCallable();
		ExecutorService executor = Executors.newFixedThreadPool(3);
		Future<?> f = executor.submit(c);
		synchronized(f) {
			System.out.println("callable이 종료될 때까지 기다립니다.");
			executor.wait();
			executor.shutdownNow();
		}
		System.out.println("*****************      " + f);
		
		// 메인 쓰레드
		Thread.sleep(1000);
		System.out.format("- Main:END: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(),Thread.currentThread().getName());
		
	}
}

class ThreadB extends Thread {
	int sum = 0;
	public void run() {
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sum += i;
			System.out.println("beep beep beep ==> " + this.getName());
		}
		System.out.println("ThreadB: inside ThreadB sum = " + sum);
		System.out.format("- ThreadB: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", this.currentThread(), this.activeCount(), this.currentThread().getId(), this.getName());
	}
	
	public int getSum() {
		return this.sum;
	}
}

class ThreadSyncWMethod extends Thread {
	public synchronized void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < 10; i++) System.out.println("ThreadSyncWMethod beep!!! -> " + i);
		System.out.format("- ThreadSyncWMethod: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", this.currentThread(), this.activeCount(), this.currentThread().getId(), this.getName());
		this.notify();
	}
}

class ThreadSyncWObject extends Thread {
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (this) {
				for (int i = 0; i < 10; i++) System.out.println("ThreadSyncWObject beep!!! -> " + i);;				
				this.notify();
			}
			System.out.format("- ThreadSyncWObject: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", this.currentThread(), this.activeCount(), this.currentThread().getId(), this.getName());
		}
}

class ThreadRunnable implements Runnable {

	@Override
	public void run() {
		System.out.format("- Runnable : CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(), Thread.currentThread().getName());
	}
	
}

class ThreadCallable implements Callable<StringBuffer> {

	@Override
	public synchronized StringBuffer call() throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sb.append("C: " + Math.random()*10 + " +++ ");
			System.out.println(sb.toString());
			System.out.format("->> Callable : CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(), Thread.currentThread().getName());

		}
		notify();
		
		return sb;
	}
	
}


/**
 * @author victor
 * Daemon Thread that runs background
 */
class DaemonThread implements Runnable {

	@Override
	public void run() {
		while (true) {
			System.out.format("- DaemonThread 실행 중: CurrentThread = %s, ActiveThread = %s, ThreadID = %d, ThreadName = %s \n", Thread.currentThread(), Thread.activeCount(), Thread.currentThread().getId(),Thread.currentThread().getName());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}