import { useState, useEffect, useCallback } from "react";

/**
 * Ejecuta `fetcher` (que debe devolver directamente los datos, p.ej.
 * () => api.get("/x").then(r => r.data)) y expone estado de carga/error.
 */
export function useFetch(fetcher, deps = []) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    setError(null);
    fetcher()
      .then((res) => setData(res))
      .catch((err) => setError(err))
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    load();
  }, [load]);

  return { data, loading, error, reload: load };
}
