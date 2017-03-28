package com.ahuazhu.soy.processor;

import com.ahuazhu.soy.exception.SoyException;
import com.ahuazhu.soy.modal.RequestContext;
import com.ahuazhu.soy.modal.ResponseContext;
import com.ahuazhu.soy.utils.RecordBuilder;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */
public class MockProcessor implements Processor {

    @Override
    public void process(RequestContext request,
                        ResponseContext response,
                        ProcessorChain chain) throws SoyException {
        try {

            DatagramPacket inPacket = request.getUdpPacket();

            Message query = new Message(inPacket.getData());

            Message answer = new Message(query.getHeader().getID());
            Record question = query.getQuestion();

            Record record = new RecordBuilder()
                    .dclass(question.getDClass())
                    .name(question.getName())
                    .answer("10.11.12.13")
                    .type(question.getType())
                    .toRecord();

            answer.addRecord(record, Section.ANSWER);

            byte[] answerData = answer.toWire();
            DatagramPacket outPacket = new DatagramPacket(answerData,
                    answerData.length, inPacket.getAddress(),
                    inPacket.getPort());

            outPacket.setData(answerData);
            outPacket.setLength(answerData.length);
            outPacket.setAddress(inPacket.getAddress());
            outPacket.setPort(inPacket.getPort());
            request.getUdpSocket().send(outPacket);
        } catch (IOException e) {
            throw new SoyException();
        }

        chain.process(request, response);

    }
}
