package io.bacta.game.object.tangible.creature;

import akka.japi.pf.ReceiveBuilder;
import io.bacta.game.object.tangible.TangibleObjectActor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatureObjectActor extends TangibleObjectActor<CreatureObject> {
    protected CreatureObjectActor(CreatureObject creatureObject) {
        super(creatureObject);
    }

    @Override
    protected ReceiveBuilder appendReceiveHandlers(ReceiveBuilder receiveBuilder) {
        return super.appendReceiveHandlers(
                receiveBuilder
//                        .match(MailMessage.class, this::mailMessage)
//                        .match(SystemMessage.class, this::systemMessage)
//                        .match(CombatSpam.class, this::combatSpam)
//                        .match(WithdrawalReq.class, this::withdraw)
//                        .match(DepositReq.class, this::deposit)
        );
    }
//
//    private void combatSpam(CombatSpam combatSpam) {
//        System.out.println(String.format("%s combat spams: %s", object.getObjectName(), combatSpam.toString()));
//    }
//
//    private void systemMessage(SystemMessage systemMessage) {
//        System.out.println(String.format("%s system message: %s", object.getObjectName(), systemMessage.getMessage()));
//    }
//
//    private void mailMessage(MailMessage mailMessage) {
//        System.out.println(String.format("%s mail: %s with subject %s", object.getObjectName(), mailMessage.getFrom(), mailMessage.getSubject()));
//        System.out.println(mailMessage.getMessage());
//    }
//
//    private void withdraw(WithdrawalReq msg) {
//        final long transactionId = msg.getTransactionId();
//        final String name = object.getObjectName();
//        final int cash = object.getCash();
//        final int bank = object.getBank();
//
//        if (cash < msg.getCash() || bank < msg.getBank()) {
//            final WithdrawalFailed failMessage = new WithdrawalFailed(transactionId, "Insufficient funds available.", name);
//            sender().tell(failMessage, self());
//            return;
//        }
//
//        object.setCash(cash - msg.getCash());
//        object.setBank(bank - msg.getBank());
//
//        if (msg.isAck()) {
//            final WithdrawalAck ack = msg.toAck(name);
//            sender().tell(ack, self());
//        }
//    }
//
//    private void deposit(DepositReq msg) {
//        final long transactionId = msg.getTransactionId();
//        final String name = object.getObjectName();
//        final long targetCash = object.getCash() + msg.getCash();
//        final long targetBank = object.getBank() + msg.getBank();
//
//        if (targetCash > Integer.MAX_VALUE || targetBank > Integer.MAX_VALUE) {
//            final DepositFailed failMessage = new DepositFailed(
//                    transactionId, "Insufficient storage for funds. Value would be truncated.", name);
//            sender().tell(failMessage, self());
//            return;
//        }
//
//        object.setCash((int) targetCash);
//        object.setBank((int) targetBank);
//
//        if (msg.isAck()) {
//            final DepositAck ack = msg.toAck(name);
//            sender().tell(ack, self()); //Just resent the msg back to the sender.
//        }
//    }
}