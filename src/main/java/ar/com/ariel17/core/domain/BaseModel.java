package ar.com.ariel17.core.domain;

import java.util.Date;

public abstract class BaseModel<T> {

    protected T id;

    protected Date createdAt;

    public void setId(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
