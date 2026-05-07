DELETE FROM capacidad_residual_proponente
WHERE COALESCE(resultado_k, 0) = 0
  AND COALESCE(capacidad_organizacion, 0) = 0
  AND COALESCE(experiencia, 0) = 0
  AND COALESCE(capacidad_tecnica, 0) = 0
  AND COALESCE(capacidad_financiera, 0) = 0
  AND COALESCE(saldos_contratos_ejecucion, 0) = 0;
