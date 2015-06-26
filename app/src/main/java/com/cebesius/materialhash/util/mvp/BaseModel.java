package com.cebesius.materialhash.util.mvp;

import android.os.Bundle;

public abstract class BaseModel implements Model {

    public abstract void start();

    public abstract void stop();

    public abstract void saveState(Bundle bundle);

    public abstract void restoreState(Bundle bundle);
}
