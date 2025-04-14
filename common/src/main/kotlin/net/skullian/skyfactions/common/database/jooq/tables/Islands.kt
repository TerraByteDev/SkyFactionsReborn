/*
 * This file is generated by jOOQ.
 */
package net.skullian.skyfactions.common.database.jooq.tables


import kotlin.collections.Collection

import net.skullian.skyfactions.common.database.jooq.DefaultSchema
import net.skullian.skyfactions.common.database.jooq.keys.ISLANDS__PK_ISLANDS
import net.skullian.skyfactions.common.database.jooq.tables.records.IslandsRecord

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("warnings")
open class Islands(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, IslandsRecord>?,
    parentPath: InverseForeignKey<out Record, IslandsRecord>?,
    aliased: Table<IslandsRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<IslandsRecord>(
    alias,
    DefaultSchema.DEFAULT_SCHEMA,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>islands</code>
         */
        val ISLANDS: Islands = Islands()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<IslandsRecord> = IslandsRecord::class.java

    /**
     * The column <code>islands.id</code>.
     */
    val ID: TableField<IslandsRecord, Int?> = createField(DSL.name("id"), SQLDataType.INTEGER, this, "")

    /**
     * The column <code>islands.uuid</code>.
     */
    val UUID: TableField<IslandsRecord, ByteArray?> = createField(DSL.name("uuid"), SQLDataType.BLOB, this, "")

    /**
     * The column <code>islands.last_raided</code>.
     */
    val LAST_RAIDED: TableField<IslandsRecord, Long?> = createField(DSL.name("last_raided"), SQLDataType.BIGINT, this, "")

    private constructor(alias: Name, aliased: Table<IslandsRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<IslandsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<IslandsRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>islands</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>islands</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>islands</code> table reference
     */
    constructor(): this(DSL.name("islands"), null)
    override fun getSchema(): Schema? = if (aliased()) null else DefaultSchema.DEFAULT_SCHEMA
    override fun getPrimaryKey(): UniqueKey<IslandsRecord> = ISLANDS__PK_ISLANDS
    override fun `as`(alias: String): Islands = Islands(DSL.name(alias), this)
    override fun `as`(alias: Name): Islands = Islands(alias, this)
    override fun `as`(alias: Table<*>): Islands = Islands(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Islands = Islands(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Islands = Islands(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Islands = Islands(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Islands = Islands(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Islands = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Islands = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Islands = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Islands = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Islands = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Islands = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Islands = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Islands = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Islands = where(DSL.notExists(select))
}
