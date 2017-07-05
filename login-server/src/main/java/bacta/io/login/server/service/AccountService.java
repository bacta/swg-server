package bacta.io.login.server.service;

/**
 * Created by crush on 7/2/2017.
 */
public interface AccountService {
    void validateAccount(int bactaId,
                         int clusterId,
                         int subscriptionBits,
                         boolean canCreateRegular,
                         boolean canCreateJedi,
                         boolean canSkipJedi,
                         boolean canSkipTutorial);

    //Account registerAccount(String username, String password) throws AccountRegistrationFailedException;
}

//public class AccountService {
//    private final AccountRepository accountRepository;
//    private final ClusterService clusterService;
//    private final LoginServerProperties loginServerProperties;
//
//    public AccountService(AccountRepository accountRepository,
//                          ClusterService clusterService,
//                          LoginServerProperties loginServerProperties) {
//        this.accountRepository = accountRepository;
//        this.clusterService = clusterService;
//        this.loginServerProperties = loginServerProperties;
//    }
//
//    /**
//     * Validates whether a particular account can connect to a particular cluster.
//     * Also, flag whether character creation is allowed.
//     *
//     * @param bactaId
//     * @param clusterId
//     * @param subscriptionBits
//     * @param canCreateRegular
//     * @param canCreateJedi
//     * @param canSkipJedi
//     * @param canSkipTutorial
//     */
//    public void validateAccount(final int bactaId,
//                                final int clusterId,
//                                final int subscriptionBits,
//                                boolean canCreateRegular,
//                                boolean canCreateJedi,
//                                boolean canSkipJedi,
//                                boolean canSkipTutorial) {
//
//        boolean canLogin = false;
//
//        final ClusterListEntry clusterListEntry = clusterService.findClusterById(clusterId);
//
//        if (clusterListEntry != null /* && clusterListEntry->centralServerConnection != null*/) {
//
//            boolean clientIsInternal = false;
//            /*
//            ClientConnection* connection = getValidatedClient(bactaId);
//
//            if (connection != null) {
//                clientIsInternal = AdminAccountManager.isInternalIp(connection.getRemoteAddress());
//            }
//            */
//
//            if (clientIsInternal && loginServerProperties.isInternalBypassOnlineLimitEnabled()) {
//                canLogin = true;
//            } else if (clusterListEntry.getNumPlayers() <= clusterListEntry.getOnlinePlayerLimit()) {
//                canLogin = true;
//
//                //check cluster npe user limit
//                if (clusterListEntry.getNumTutorialPlayers() > clusterListEntry.getOnlineTutorialLimit()) {
//                    canCreateRegular = false;
//                    canCreateJedi = false;
//                }
//
//                //limit login/character creation based on subscription feature bits.
//                if (((subscriptionBits & ClientSubscriptionFeature.FREE_TRIAL) != 0)
//                    && ((subscriptionBits & ClientSubscriptionFeature.BASE) == 0)) {
//
//                    if (clusterListEntry.getNumFreeTrialPlayers() > clusterListEntry.getOnlineFreeTrialLimit()) {
//                        canLogin = false;
//                    }
//
//                    if (!clusterListEntry.isFreeTrialCanCreateChar()) {
//                        canCreateRegular = false;
//                        canCreateJedi = false;
//                    }
//                }
//            }
//
//            if (!canLogin) {
//                canCreateRegular = false;
//                canCreateJedi = false;
//                canSkipTutorial = false;
//            }
//
//            if (loginServerProperties.isSkippingTutorialAllowedForAll())
//                canSkipTutorial = true;
//
//            //Send a message to the galaxy server to find out if this account is allowed to login to the requested cluster.
//            //The central server will send us back a response which will continue the login process.
//            //ValidateAccountReplyMessage msg = new ValidateAccountReplyMessage(stationId, canLogin, canCreateRegular, canCreateJedi, canSkipTutorial);
//            //clusterListEntry.centralServerConnection.send(msg);
//        }
//    }
//
//
//    public Account createAccount(String username, String password) {
//        LOGGER.info("Creating an account for username {}", username);
//
//        final String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
//
//        Account account = new Account(username, passwordHash);
//        account = accountRepository.save(account);
//
//        LOGGER.debug("Created new account for username {} with id {} and password hash {}", username, account.getId(), passwordHash);
//
//        return account;
//    }
//}
