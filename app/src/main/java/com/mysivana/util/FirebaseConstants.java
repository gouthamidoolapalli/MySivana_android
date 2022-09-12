package com.mysivana.util;

public interface FirebaseConstants {
    //Tour Screen
    String LAUNCH_TOUR = "LaunchTour";
    String CLICK_PREV_TOUR = "ClickPrevTour";
    String CLICK_NEXT_TOUR = "ClickNextTour";
    String CLICK_SKIP_TOUR = "ClickSkipTour";

    //Log In Screen
    String LAUNCH_LOG_IN = "LaunchLogIn";
    String CLICK_FORGOT_PWD = "ClickForgotPwd";
    String CLICK_SIGN_IN = "ClickSignIn";
    String CLICK_SIGN_UP = "ClickSignUp";

    //Bundle param for Sign In in Login screen
    String PARAM_EMAIL = "ParamEmail";
    String PARAM_PHONE = "ParamPhone";
    String PARAM_USER_ID = "ParamUserId";

    //Forgot Password Screen
    String LAUNCH_FORGOT_PWD = "LaunchForgotPwd";
    String CLICK_SEND_PWD = "ClickSendPwd";

    //OTP For Forgot Password
    String LAUNCH_OTP_FORGOT_PWD = "LaunchOTPForgotPwd";
    String OTP_TOKEN = "OTPTokenReceived";
    String CLICK_RESEND_OTP = "ClickResendOTP";
    String CLICK_CONTINUE_OTP = "ClickContinueOTP";

    //Bundle Param for otp token
    String PARAM_OTP_TOKEN = "ParamOTPToken";

    //New Password/Change Password
    String LAUNCH_NEW_PWD = "LaunchNewPwd";
    String CLICK_SEND_NEW_PWD = "ClickSendNewPwd";

    //Create Account
    String LAUNCH_SIGN_UP = "LaunchSignUp";
    String CLICK_SIGN_UP_MERCHANT = "ClickSignUpMerchant";
    String CLICK_SING_UP_USER = "ClickSignUpUser";
    String CLICK_SIGN_UP_DETAILS = "ClickSignUpDetails";
    String CLICK_LOCATION_SELECTED = "ClickLocationSelected";

    //OTP for Create Account
    String LAUNCH_OTP_SIGN_UP_PWD = "LaunchOTPSignUpwd";
    String SIGN_UP_OTP_TOKEN = "SignUpOtpReceived";


    //Welcome Screen After Login
    String LAUNCH_WELCOME_PAGE = "LaunchWelcomePage";

    //Dashboard Merchant Screen
    String LAUNCH_DASHBOARD_MERCHANT = "LaunchMerchantDashboard";
    String USER_TYPE_MERCHANT = "MerchantType";
    String MER_CONFIRM_PAYMENT = "MerConfirmPayment";
    String MER_PROCEED_PAYMENT = "MerProceedPayment";
    String MER_PAYMENT_COMPLETE = "MerPaymentComplete";

    //Common Dashboard Clicks
    String CLICK_DRAWER_PROFILE = "ClickDrawerProfile";
    String CLICK_DRAWER_TRANSACTIONS = "ClickDrawerTransactions";
    String CLICK_DRAWER_LOOK_UP = "ClickDrawerLookUp";
    String CLICK_DRAWER_LEGAL = "ClickDrawerLegal";
    String CLICK_DRAWER_HELP = "ClickDrawerHelp";
    String CLICK_DRAWER_SIGN_OUT = "ClickDrawerSignOut";
    String CLICK_DRAWER_BALANCE = "ClickDrawerBalance";
    String CLICK_DRAWER_INVITE = "ClickDrawerInvite";
    String CLICK_DRAWER_LEADERBOARD = "ClickDrawerLeaderboard";
    String CLICK_DRAWER_REQUEST_MONEY = "ClickDrawerRequestMoney";
    String COPY_BTC_ADDRESS = "CopyBTCAddress";
    String BTC_BREAK_UP = "BTCBreakUp";
    String VERIFY_EMAIL = "VerifyEmail";
    String ADD_BANK_DETAILS = "AddBankDetails";
    String PAYMENT_NOT_RECEIVED = "PaymentNotReceived";
    String CLICK_DRAWER_MERCHANT_LIST = "ClickDrawerMerchantList";

    //Dashboard User Screen
    String LAUNCH_DASHBOARD_USER = "LaunchUserDashboard";
    String USR_TYPE_USER = "UserType";
    String USR_CONFIRM_PAYMENT = "UserConfirmPayment";
    String USR_PROCEED_PAYMENT = "UserProceedPayment";
    String USR_PAYMENT_COMPLETE = "UserPaymentComplete";
    String TRANSACTION_TYPE = "TransactionType";
    String SAVE_BANK_DETAILS_TRANS = "SaveBankDetailsTransaction";

    //Parms for dashboard
    String PARAM_TRANS_TYPE = "ParamTransType";


    //OTP For Complete Payment
    String LAUNCH_OTP_COMPLETE_PAY = "LaunchOTPCompletePay";
    String COMPLETE_PAY_OTP_TOKEN = "CompletePayOTPTokenReceived";

    //Transaction Receipt Screen
    String LAUNCH_TRANSACTION_RECEIPT = "LaunchTransactionReceipt";
    String CLICK_SEND_RECEIPT = "ClickSendReceipt";
    String CLICK_HERE_REPORT = "ClickHereToReport";

    //Transaction List Screen
    String LAUNCH_TRANSACTION_LIST = "LaunchTransactionList";
    String CLICK_VIEW_RECEIPT = "ClickViewReceipt";
    String CLICK_ONGOING = "ClickOngoing";
    String CLICK_COMPLETED = "ClickCompleted";

    //Report issue Screen
    String LAUNCH_REPORT_ISSUE = "LaunchReportIssue";
    String CLICK_SEND_REPORT = "ClickSendReport";

    //UserProfile Screen
    String LAUNCH_USER_PROFILE = "LaunchUserProfile";
    String CLICK_CHANGE_DP = "ClickChangeDP";
    String CLICK_CHANGE_PWD = "ClickChangePWD";
    String CLICK_VIEW_BANK_INFO = "ClickViewBankInfo";
    String CLICK_VIEW_BALANCE = "ClickViewBalance";

    //Transaction Lookup
    String LAUNCH_TRANSACTION_LOOKUP = "LaunchTransactionLookUp";
    String CLICK_PAY_CASH = "ClickPayCash";

    //Wallet Balance Screen
    String LAUNCH_WALLET_BALANCE = "LaunchWalletBalance";
    String CLICK_REQUEST_STATEMENT = "ClickRequestStatement";

    //Help Screen
    String LAUNCH_HELP = "LaunchHelp";
    String CLICK_CALL = "ClickCall";
    String CLICK_SEND_HELP = "ClickSendHelp";
    String CLICK_HELP = "ClickHelp";

    //Legal Screen
    String LAUNCH_LEGAL = "LaunchLegal";
    String CLICK_TERMS = "ClickTerms";
    String CLICK_PRIVACY = "ClickPrivacy";
    String CLICK_COPYRIGHT = "ClickCopyright";

    //Merchant List Screen
    String LAUNCH_MERCHANT_LIST = "LaunchMerchantList";
    String CLICK_CALL_MERCHANT = "ClickCallMerchant";
    String CLICK_GET_DIRECTIONS = "ClickGetDirections";

    //Add Bank Details Screen
    String LAUNCH_BANK_DETAILS = "LaunchBankDetails";
    String CLICK_EDIT_BANK_DETAILS = "ClickEditBankDetails";
    String CLICK_SAVE_BANK_DETAILS = "ClickSaveBankDetails";
    String CLICK_CONFIRM_PASSWORD = "ClickConfirmPassword";

    //Leader board screen
    String LAUNCH_LEADER_BOARD = "LaunchLeaderBoard";

    //InviteFriend Screen
    String LAUNCH_INVITE_FIREND = "LaunchInviteFriend";
    String CLICK_INVITE_FRIENDS = "ClickInviteFriend";
    String CLICK_INVITE_RULES = "ClickInviteRules";
    String INVITE_CODE = "InviteCode";
    String LAUNCH_BUY_BITCOINS = "LaunchBuyBitcoins";

    //Request Cash
    String LAUNCH_REQUEST_MONEY = "LaunchRequestCash";
    String CLICK_OPEN_CONTACTS = "ClickOpenContacts";
    String CLICK_REQUEST_CASH = "ClickRequestCash";
    String CLICK_ATTACHMENTS = "ClickAttachments";

    //Notifications Screen
    String LAUNCH_NOTIFICATION_SCREEN = "LaunchNotificationScreen";

    String LAUNCH_ATTACHMENTS_SCREEN = "LaunchAttachmentsScreen";
}
