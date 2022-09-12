package com.mysivana.mvp.model;

import com.squareup.moshi.Json;

import java.util.List;

public class ReferralBoardResponse {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private int httpStatusCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private Value value;

    public String getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(String transactionFlag) {
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


    public static class UserScore {

        @Json(name = "userId")
        private int userId;
        @Json(name = "fullName")
        private String fullName;
        @Json(name = "profileUrl")
        private String profileUrl;
        @Json(name = "totalUserReferred")
        private String totalUserReferred;
        @Json(name = "mysivanaScore")
        private int mysivanaScore;
        @Json(name = "userRank")
        private int userRank;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public String getTotalUserReferred() {
            return totalUserReferred;
        }

        public void setTotalUserReferred(String totalUserReferred) {
            this.totalUserReferred = totalUserReferred;
        }

        public int getMysivanaScore() {
            return mysivanaScore;
        }

        public void setMysivanaScore(int mysivanaScore) {
            this.mysivanaScore = mysivanaScore;
        }

        public int getUserRank() {
            return userRank;
        }

        public void setUserRank(int userRank) {
            this.userRank = userRank;
        }
    }

    public static class Value {

        @Json(name = "totalUsers")
        private int totalUsers;
        @Json(name = "userScores")
        private List<UserScore> userScores = null;
        @Json(name = "myScore")
        private UserScore myScore;

        public UserScore getMyScore() {
            return myScore;
        }

        public void setMyScore(UserScore myScore) {
            this.myScore = myScore;
        }

        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public List<UserScore> getUserScores() {
            return userScores;
        }

        public void setUserScores(List<UserScore> userScores) {
            this.userScores = userScores;
        }
    }
}
