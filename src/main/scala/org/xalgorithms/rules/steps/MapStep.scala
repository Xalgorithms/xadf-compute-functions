package org.xalgorithms.rules.steps

import org.xalgorithms.rules.{ Context }
import org.xalgorithms.rules.elements._

class MapStep(table: Reference, assignments: Seq[Assignment]) extends AssignmentStep(table, assignments) {
  def execute(ctx: Context) {
    val tbl = ctx.find_in_section(table.section, table.key)
    ctx.retain(table.section, table.key, tbl.map { row =>
      row ++ assignments.foldLeft(Map[String, Value]()) { (o, ass) =>
        val av = resolve_to_atomic_value(row, ass.source)

        if (null != av) {
          o ++ Map(ass.target -> av)
        } else {
          println("DEBT: unsupported or unknown source")
          o
        }
      }
    })
  }

  def resolve_to_atomic_value(row: Map[String, Value], v: Value): Value = v match {
    case (rv: Reference) => if (rv.section == "_context") row.getOrElse(rv.key, null) else null
    case (fv: FunctionValue) => apply(fv.name, fv.args.map(arg => resolve_to_atomic_value(row, arg)))
    case _ => v
  }

  def apply(fn: String, args: Seq[Value]): Value = fn match {
    case "add" => apply_add(args)
    case _ => new EmptyValue
  }

  def apply_add(args: Seq[Value]): Value = {
    new NumberValue(args.map(make_number).foldLeft(BigDecimal(0.0)) { (sum, n) => sum + n })
  }

  def make_number(v: Value): BigDecimal = v match {
    case (nv: NumberValue) => nv.value
    case (sv: StringValue) => BigDecimal(sv.value)
    case _ => 0.0
  }
}
