package dev.komu.ahwen.parse

import dev.komu.ahwen.query.Expression
import dev.komu.ahwen.query.Predicate
import dev.komu.ahwen.query.SqlValue
import dev.komu.ahwen.record.Schema
import dev.komu.ahwen.types.ColumnName
import dev.komu.ahwen.types.IndexName
import dev.komu.ahwen.types.TableName

sealed class CommandData

class InsertData(val table: TableName, val fields: List<ColumnName>, val values: List<SqlValue>) : CommandData()

class DeleteData(val table: TableName, val predicate: Predicate) : CommandData()

class ModifyData(val table: TableName, val fieldName: ColumnName, val newValue: Expression, val predicate: Predicate) : CommandData()

class QueryData(
    val fields: List<ColumnName>,
    val tables: List<TableName>,
    val predicate: Predicate,
    val orderBy: List<ColumnName>
    ) {
    override fun toString(): String = buildString {
        append("select ")
        fields.joinTo(this, separator = ", ")
        append(" from ")
        tables.joinTo(this, separator = ", ")

        val predString = predicate.toString()
        if (predString.isNotEmpty())
            append(" where ").append(predString)

        if (orderBy.isNotEmpty()) {
            append(" order by ")
            orderBy.joinTo(this, separator = ", ")
        }
    }
}

class CreateTableData(val table: TableName, val schema: Schema) : CommandData()

class CreateViewData(val view: TableName, private val query: QueryData) : CommandData() {
    val viewDefinition: String
        get() = query.toString()
}

class CreateIndexData(val index: IndexName, val table: TableName, val field: ColumnName) : CommandData()
