/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 */

package com.sun.grizzly.util;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO: pub set methods should call reconfigure.
 *
 * TODO: find a way to override executorservice in both java 1.5 1.6,
 * until then the code is commented out to not break compile.
 *
 * @author gustav trede
 * @author Alexey Stashok
 */
public class ThreadPoolFactory {

   /* public static ExtendedReconfigurableThreadPool getInstance(
            ThreadPoolConfig config){
        return new PoolWrap(config);
    }

    final static class PoolWrap implements ExtendedReconfigurableThreadPool{
        private volatile ExtendedThreadPool pool;
        private volatile ThreadPoolConfig config;
        private final Object statelock = new Object();

        public PoolWrap(ThreadPoolConfig config) {
            if (config == null)
                throw new IllegalArgumentException("config is null");
            this.pool   = getImpl(config);
            this.config = config;
        }

        private final ExtendedThreadPool getImpl(ThreadPoolConfig cfg){
            if (cfg.corepoolsize < 0 || cfg.corepoolsize==cfg.maxpoolsize){
                return cfg.queuelimit < 1 ?
                    new FixedThreadPool(cfg.poolname, cfg.maxpoolsize,
                    cfg.queue, cfg.threadFactory,cfg.monitoringProbe) :
                    new QueueLimitedThreadPool(
                    cfg.poolname,cfg.maxpoolsize,cfg.queuelimit,
                    cfg.threadFactory,cfg.queue,cfg.monitoringProbe);
            }
            return new SyncThreadPool(cfg.poolname, cfg.corepoolsize,
                    cfg.maxpoolsize,cfg.keepAliveTime, cfg.timeUnit,
                    cfg.threadFactory, cfg.queue, cfg.queuelimit);
        }

        public final void reconfigure(ThreadPoolConfig config) {
            if (config == null)
                throw new IllegalArgumentException("config is null");
            synchronized(statelock){
                //TODO: only create new pool if old one cant be runtime config.
                ExtendedThreadPool oldpool = this.pool;                
                this.pool = getImpl(config);
                AbstractThreadPool.drain(oldpool.getQueue(), pool.getQueue());
                oldpool.shutdown();
                this.config = config;
            }
        }

        public ThreadPoolConfig getConfiguration() {
            return config;
        }
        
        public int getActiveCount() {
            return pool.getActiveCount();
        }

        public int getTaskCount() {
            return pool.getTaskCount();
        }

        public long getCompletedTaskCount() {
            return pool.getCompletedTaskCount();
        }

        public int getCorePoolSize() {
            return pool.getCorePoolSize();
        }

        public void setCorePoolSize(int corePoolSize) {
            
        }

        public int getLargestPoolSize() {
            return pool.getLargestPoolSize();
        }

        public int getPoolSize() {
            return pool.getPoolSize();
        }

        public Queue<Runnable> getQueue() {
            return pool.getQueue();
        }

        public int getQueueSize() {
            return pool.getQueueSize();
        }

        public long getKeepAliveTime(TimeUnit unit) {
            return pool.getKeepAliveTime(unit);
        }

        public void setKeepAliveTime(long time, TimeUnit unit) {
            pool.setKeepAliveTime(time, unit);
        }

        public int getMaximumPoolSize() {
            return pool.getMaximumPoolSize();
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            //throw new UnsupportedOperationException();
        }

        public int getMaxQueuedTasksCount() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setMaxQueuedTasksCount(int maxTasksCount) {
            //throw new UnsupportedOperationException();
        }

        public String getName() {
            return pool.getName();
        }

        public void setName(String name) {
            pool.setName(name);
        }

        public void setThreadFactory(ThreadFactory threadFactory) {
            pool.setThreadFactory(threadFactory);
        }

        public ThreadFactory getThreadFactory() {
            return pool.getThreadFactory();
        }

        public void shutdown() {
            pool.shutdown();
        }

        public List<Runnable> shutdownNow() {
            return pool.shutdownNow();
        }

        public final boolean isShutdown() {
            return pool.isShutdown();
        }

        public final boolean isTerminated() {
            return pool.isTerminated();
        }

        public final boolean awaitTermination(long l, TimeUnit tu)
                throws InterruptedException {
            return pool.awaitTermination(l, tu);
        }

        public final <T> Future<T> submit(Callable<T> clbl) {
            return pool.submit(clbl);
        }

        public final <T> Future<T> submit(Runnable r, T t) {
            return pool.submit(r, t);
        }

        public final Future<?> submit(Runnable r) {
            return pool.submit(r);
        }

        public final void execute(Runnable r) {
            pool.execute(r);
        }

        //broken 1.5 compile
        public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return pool.invokeAll(tasks);
        }

        public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return pool.invokeAll(tasks, timeout, unit);
        }

        public final <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
             return pool.invokeAny(tasks);
        }

        public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
             return pool.invokeAny(tasks, timeout, unit);
        }
    }*/
}