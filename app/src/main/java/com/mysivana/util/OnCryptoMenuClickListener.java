package com.mysivana.util;

import com.mysivana.mvp.model.CryptoMenuResponse;

public interface OnCryptoMenuClickListener {
    void onItemClick(CryptoMenuResponse.Value item);
}
