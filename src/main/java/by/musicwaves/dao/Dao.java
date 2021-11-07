package by.musicwaves.dao;
import java.util.List;

public interface Dao<T>
{
    List<T> getAll() throws DaoException;
    public T findById(int id) throws DaoException;
    // todo: change to int?
    public Integer create(T instance) throws DaoException;
    public void update(T instance) throws DaoException;
    public boolean deleteById(int id) throws DaoException;
    public boolean delete(T instance) throws DaoException;

}
