package com.ahuazhu.soy;

import co.paralleluniverse.fibers.FiberUtil;
import co.paralleluniverse.fibers.SuspendExecution;
import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.Request;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.processor.SimpleProcessorChain;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class Soy {

    public static void fire(Request request) throws SoyException {
        final Commands command = new Commands(request);
        try {
            FiberUtil.runInFiber(command::process);
        } catch (Exception e) {
            throw new SoyException();
        }
    }

    private static class Commands {

        private Request request;

        public Commands(Request request) {
            this.request = request;
        }

        public void process() throws SuspendExecution, InterruptedException {
            SimpleProcessorChain.create().process(new RequestContext(request), null);
        }
    }
}
