package com.mysivana.mvp.model;

import com.squareup.moshi.Json;

import java.util.List;

public class NotificationListResponse {
    @Json(name = "transactionFlag")
    private Object transactionFlag;
    @Json(name = "httpStatusCode")
    private int httpStatusCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private List<Value> value = null;

    public Object getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Object transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

    public static class Value {

        @Json(name = "userId")
        private int userId;
        @Json(name = "message")
        private String message;
        @Json(name = "notificationType")
        private int notificationType;
        @Json(name = "receiverId")
        private int receiverId;
        @Json(name = "senderPhoneNumber")
        private String senderPhoneNumber;
        @Json(name = "amountInFiat")
        private int amountInFiat;
        @Json(name = "createdDate")
        private String createdDate;
        @Json(name = "receiverFullName")
        private String receiverFullName;
        @Json(name = "receiverPhoneNumber")
        private String receiverPhoneNumber;
        @Json(name = "receiverCountryCode")
        private String receiverCountryCode;
        @Json(name = "receiverEmail")
        private String receiverEmail;
        @Json(name = "transactionRequestId")
        private String transactionRequestId;
        @Json(name = "requestAttachment")
        private String attachmentUrl;
        @Json(name = "contentType")
        private String contentType;
        @Json(name = "paymentType")
        private String paymentType;

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getTransactionRequestId() {
            return transactionRequestId;
        }

        public void setTransactionRequestId(String transactionRequestId) {
            this.transactionRequestId = transactionRequestId;
        }

        public String getReceiverFullName() {
            return receiverFullName;
        }

        public void setReceiverFullName(String receiverFullName) {
            this.receiverFullName = receiverFullName;
        }

        public String getReceiverPhoneNumber() {
            return receiverPhoneNumber;
        }

        public void setReceiverPhoneNumber(String receiverPhoneNumber) {
            this.receiverPhoneNumber = receiverPhoneNumber;
        }

        public String getReceiverCountryCode() {
            return receiverCountryCode;
        }

        public void setReceiverCountryCode(String receiverCountryCode) {
            this.receiverCountryCode = receiverCountryCode;
        }

        public String getReceiverEmail() {
            return receiverEmail;
        }

        public void setReceiverEmail(String receiverEmail) {
            this.receiverEmail = receiverEmail;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(int notificationType) {
            this.notificationType = notificationType;
        }

        public int getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(int receiverId) {
            this.receiverId = receiverId;
        }

        public String getSenderPhoneNumber() {
            return senderPhoneNumber;
        }

        public void setSenderPhoneNumber(String senderPhoneNumber) {
            this.senderPhoneNumber = senderPhoneNumber;
        }

        public int getAmountInFiat() {
            return amountInFiat;
        }

        public void setAmountInFiat(int amountInFiat) {
            this.amountInFiat = amountInFiat;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getAttachmentUrl() {
            return attachmentUrl;
        }

        public void setAttachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
