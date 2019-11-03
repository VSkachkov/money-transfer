package revolut.domain.account;

public enum Status {
    IN_PROCESS,
    COMPLETED,
    FAILED_SENDER_HAS_NOT_ENOUGH_MONEY,
    FAILED_TECHNICAL_ERROR;

}
