import { useMemo, useState } from "react";
import { Icon } from "./Icon";
import { EmptyState } from "./feedback";

/**
 * Tabla CRUD estandarizada: buscador global, filtros por columna y ordenamiento.
 *
 * columns: [{ key, label, sortable?, render?(row) }]
 * filters: [{ key, label, options:[{value,label}] }]
 * searchKeys: [keys] usados por el buscador global
 */
export function DataTable({
  columns,
  data = [],
  searchKeys = [],
  filters = [],
  toolbar,
  emptyText = "Sin registros",
  onRowClick,
  rowId = (row, i) => row.id ?? i,
}) {
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState({ key: null, dir: "asc" });
  const [active, setActive] = useState({});

  const filtered = useMemo(() => {
    let rows = [...data];
    const q = search.trim().toLowerCase();
    if (q && searchKeys.length) {
      rows = rows.filter((r) => searchKeys.some((k) => String(r[k] ?? "").toLowerCase().includes(q)));
    }
    filters.forEach((f) => {
      const v = active[f.key];
      if (v) rows = rows.filter((r) => String(r[f.key]) === String(v));
    });
    if (sort.key) {
      rows.sort((a, b) => {
        const av = a[sort.key];
        const bv = b[sort.key];
        if (av === bv) return 0;
        const cmp = av > bv ? 1 : -1;
        return sort.dir === "asc" ? cmp : -cmp;
      });
    }
    return rows;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [data, search, active, sort, searchKeys, filters]);

  const toggleSort = (col) => {
    if (!col.sortable) return;
    setSort((s) =>
      s.key === col.key
        ? { key: col.key, dir: s.dir === "asc" ? "desc" : "asc" }
        : { key: col.key, dir: "asc" }
    );
  };

  return (
    <div className="card">
      <div className="table-toolbar">
        <div className="table-search">
          <Icon name="search" />
          <input
            className="form-control"
            placeholder="Buscar..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="table-filters">
          {filters.map((f) => (
            <select
              key={f.key}
              className="form-select"
              style={{ width: "auto" }}
              value={active[f.key] || ""}
              onChange={(e) => setActive((a) => ({ ...a, [f.key]: e.target.value }))}
            >
              <option value="">{f.label}</option>
              {f.options.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          ))}
        </div>
        <div className="ms-auto d-flex gap-2">{toolbar}</div>
      </div>

      <div className="table-scroll">
        <table className="table table-hover table-r911 align-middle mb-0">
          <thead>
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={col.sortable ? "th-sort" : ""}
                  onClick={() => toggleSort(col)}
                >
                  {col.label}
                  {col.sortable && (
                    <Icon
                      name={
                        sort.key === col.key
                          ? sort.dir === "asc"
                            ? "arrow-up"
                            : "arrow-down"
                          : "arrow-down-up"
                      }
                    />
                  )}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={columns.length}>
                  <EmptyState icon="search" title="Sin resultados" message={emptyText} />
                </td>
              </tr>
            ) : (
              filtered.map((row, i) => (
                <tr
                  key={rowId(row, i)}
                  onClick={onRowClick ? () => onRowClick(row) : undefined}
                  style={onRowClick ? { cursor: "pointer" } : undefined}
                >
                  {columns.map((col) => (
                    <td key={col.key}>{col.render ? col.render(row) : row[col.key]}</td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
