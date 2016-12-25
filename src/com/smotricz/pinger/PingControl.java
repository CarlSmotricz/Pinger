package com.smotricz.pinger;

import java.net.InetAddress;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

public class PingControl {

	// ========== STATIC

	// ----- Constants
	
	/** Time between pings */
	public static final long INTERVAL = 5000;
	
	/** Time after which a ping is considered failed. */
	public final static int TIMEOUT = 5000;
	
	/** Amount of data to test with. 
	 *  32 appears to be a "normal" small size. 
	 */
	public final static int PACKET_SIZE = 32;
	
	/** Time-to-live in number of hops.
	 *  The absolute limit is 255.
	 */
	public final static int TTL = 250;
	
	// ----- Class data:
	
	private static Map<InetAddress, PingControl> pingers = new HashMap<InetAddress, PingControl>();
	private static ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * Fire off a ping request for the given address.
	 * @param addr
	 */
	public static void doPing(InetAddress addr) {
		if (!pingers.containsKey(addr)) {
			pingers.put(addr, new PingControl(addr));
		}
		pingers.get(addr).startPing();
	}

	/**
	 * Retrieve an Iterator of responses for the given address.
	 * The Iterator starts with the most recent response, so the caller can
	 * choose to process just a subset of responses.
	 * @param addr
	 * @return
	 */
	public static Iterator<PingResponse> responseIterator(InetAddress addr) {
		if (pingers.containsKey(addr)) {
			return pingers.get(addr).responses.descendingIterator();
		} else {
			return null;
		}
	}
	
	// ========== INSTANCE

	// ----- Instance data:
	
	public final String addrString;
	public final InetAddress addr;
	private final PingRunner runner;
	private final Deque<PingResponse> responses = new ConcurrentLinkedDeque<PingResponse>(); 

	/**
	 * Constructor.
	 * @param addr
	 */
	protected PingControl(InetAddress addr) {
		this.addr = addr;
		this.addrString = addr.getHostAddress();
		this.runner = new PingRunner(this.addrString);
	}

	/**
	 * Initiate a ping request.
	 */
	private void startPing() {
		threadPool.submit(runner);
	}

	/**
	 * Ping successful.
	 * @param timestamp
	 * @param duration
	 */
	private void onResponse(long timestamp, int duration) {
		// Log.log("%s: %d ms.", this.addrString, duration);
		responses.addLast(new PingResponse(timestamp, duration));
	}
	
	/**
	 * Ping timed out.
	 * @param timestamp
	 */
	private void onTimeout(long timestamp) {
		// Log.log("%s: TIMEOUT.", this.addrString);
		responses.addLast(new PingResponse(timestamp, TIMEOUT));
	}

	/**
	 * Ping caused an exception.
	 * @param timestamp
	 * @param t
	 */
	private void onFailure(long timestamp, Throwable t) {
		Log.log("+++ PingControl exception for host %s: %s", this.addrString, t.getMessage());
	}
	
    /**
     * Inner class: The task to perform a ping for one host.
     */
    private class PingRunner implements Runnable {

        private final IcmpPingRequest req;

        PingRunner(String host) {
            req = IcmpPingUtil.createIcmpPingRequest();
            req.setHost(host);
            req.setPacketSize(PACKET_SIZE);
            req.setTimeout(TIMEOUT);
            req.setTtl(TTL);
        }

        @Override
        public void run() {
            try {
                IcmpPingResponse resp = IcmpPingUtil.executePingRequest(req);
                long now = System.currentTimeMillis();
                if (resp.getTimeoutFlag()) {
                    onTimeout(now);
                } else {
                    onResponse(now, (int) resp.getDuration());
                }
            } catch (Throwable t) {
                long now = System.currentTimeMillis();
                onFailure(now, t);
            }
        }
    }
	
}
