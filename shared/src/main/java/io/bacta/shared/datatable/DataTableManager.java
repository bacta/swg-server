package io.bacta.shared.datatable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.tre.TreeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by crush on 11/20/2015.
 */
public final class DataTableManager {
    private static final Logger logger = LoggerFactory.getLogger(DataTableManager.class);

    private final TreeFile treeFile;
    private final Map<String, DataTable> tables;
    private final Multimap<String, Consumer<DataTable>> reloadCallbacks;

    @Inject
    public DataTableManager(final TreeFile treeFile) {
        this.treeFile = treeFile;
        this.tables = new HashMap<>();
        this.reloadCallbacks = ArrayListMultimap.create();
    }

    public void close(final String table) {
        this.tables.remove(table);
    }

    public DataTable getTable(final String table) {
        return getTable(table, true);
    }

    public DataTable getTable(final String table, boolean openIfNotFound) {
        if (this.tables.containsKey(table))
            return this.tables.get(table);

        if (openIfNotFound) {
            final DataTable dataTable = open(table);

            if (dataTable == null) {
                Preconditions.checkArgument(true, "Could not find table [%s].", table);
                return null;
            }

            return dataTable;
        }

        return null;
    }

    public DataTable reload(final String table) {
        close(table);

        final DataTable dataTable = open(table);

        if (dataTable != null) {
            final Collection<Consumer<DataTable>> reloadCallbacks = this.reloadCallbacks.get(table);

            for (Consumer<DataTable> callback : reloadCallbacks)
                callback.accept(dataTable);
        }

        return dataTable;
    }

    public DataTable reloadIfOpen(final String table) {
        if (isOpen(table))
            return reload(table);
        else
            return null;
    }

    public void addReloadCallback(final String table, final Consumer<DataTable> callbackFunction) {
        this.reloadCallbacks.put(table, callbackFunction);
    }

    public boolean isOpen(final String table) {
        return this.tables.containsKey(table);
    }

    /**
     * If a DataTable is already loaded, then it will return that table. Otherwise, it will try to load the table
     * from the TreeFile. If the table does not exist, it will return null.
     *
     * @param table The path of the table to open.
     * @return The loaded DataTable.
     */
    private DataTable open(final String table) {
        DataTable dataTable = getTable(table, false);

        if (dataTable != null)
            return dataTable;

        if (!treeFile.exists(table)) {
            logger.warn(String.format("Could not find treefile table for open [%s].", table));
            return null;
        }

        final Iff iff = new Iff(table, treeFile.open(table));
        dataTable = new DataTable();
        dataTable.load(iff, this);

        this.tables.put(table, dataTable);

        return dataTable;
    }
}
