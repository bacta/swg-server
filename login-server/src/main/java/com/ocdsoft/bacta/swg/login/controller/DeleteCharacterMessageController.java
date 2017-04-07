package com.ocdsoft.bacta.swg.login.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.protocol.ServerType;
import com.ocdsoft.bacta.soe.protocol.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.protocol.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.protocol.network.controller.MessageHandled;
import com.ocdsoft.bacta.swg.db.AccountService;
import com.ocdsoft.bacta.swg.login.message.DeleteCharacterMessage;
import com.ocdsoft.bacta.swg.server.game.service.AccountSecurityService;
import com.ocdsoft.bacta.swg.login.message.DeleteCharacterReplyMessage;
import com.ocdsoft.bacta.swg.login.message.EnumerateCharacterId;
import com.ocdsoft.bacta.swg.shared.identity.CharacterInfo;
import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MessageHandled(handles = DeleteCharacterMessage.class, type = ServerType.LOGIN)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class DeleteCharacterMessageController implements GameNetworkMessageController<DeleteCharacterMessage> {

    private final AccountSecurityService accountSecurityService;
    private final AccountService accountService;

    @Inject
    public DeleteCharacterMessageController(final AccountSecurityService accountSecurityService,
                                            AccountService accountService) {
        this.accountSecurityService = accountSecurityService;
        this.accountService = accountService;
    }

    @Override
    public void handleIncoming(final SoeUdpConnection connection, final DeleteCharacterMessage message) {

        if(accountSecurityService.verifyCharacterOwnership(connection, message.getCharacterId())) {

            SoeAccount account = accountService.getAccount(connection.getAccountUsername());
            CharacterInfo characterInfo = account.getCharacter(message.getCharacterId());
            if(characterInfo != null) {

                // Set disabled, we will not be deleting anything here
                characterInfo.setDisabled(true);
                accountService.updateAccount(account);

                DeleteCharacterReplyMessage success = new DeleteCharacterReplyMessage(0);
                connection.sendMessage(success);

                EnumerateCharacterId enumerateCharacterId = new EnumerateCharacterId(account);
                connection.sendMessage(enumerateCharacterId);

                log.info("On cluster {} Set {}:{} to disabled for account {}", characterInfo.getClusterId(), characterInfo.getName(), characterInfo.getCharacterId());

                return;
            } else {
                log.error("Unable to get character info, even though account was verified {} : {}", connection.getAccountUsername(), message.getCharacterId());
            }
        }

        DeleteCharacterReplyMessage success = new DeleteCharacterReplyMessage(1);
        connection.sendMessage(success);
    }
}

