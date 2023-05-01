package ar.com.ariel17.core.domain;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public abstract class BaseModel<T> {

    protected T id;

    protected Date createdAt;

    public void setId(@NotNull T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setCreatedAt(@NotNull Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
