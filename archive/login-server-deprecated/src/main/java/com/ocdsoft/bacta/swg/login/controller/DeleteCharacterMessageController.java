package com.ocdsoft.bacta.swg.login.controller;


import com.ocdsoft.bacta.soe.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.swg.login.message.DeleteCharacterMessage;
import com.ocdsoft.bacta.swg.login.message.DeleteCharacterReplyMessage;
import com.ocdsoft.bacta.swg.login.message.EnumerateCharacterId;

import com.ocdsoft.bacta.swg.login.object.CharacterInfo;
import com.ocdsoft.bacta.swg.login.object.SoeAccount;
import com.ocdsoft.bacta.swg.login.service.AccountService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@MessageHandled(handles = DeleteCharacterMessage.class)
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

                LOGGER.info("On cluster {} Set {}:{} to disabled for account {}", characterInfo.getClusterId(), characterInfo.getName(), characterInfo.getCharacterId());

                return;
            } else {
                LOGGER.error("Unable to get character info, even though account was verified {} : {}", connection.getAccountUsername(), message.getCharacterId());
            }
        }

        DeleteCharacterReplyMessage success = new DeleteCharacterReplyMessage(1);
        connection.sendMessage(success);
    }
}

