-- Dados de demonstração para o print final.
-- Uso: docker exec -i taskmanager-mysql mysql -u"$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < scripts/seed-demo.sql

TRUNCATE TABLE tasks;

INSERT INTO tasks (uuid, name, description, status, deadline, finished_at, created_at, updated_at, deleted_at) VALUES
(UUID(), 'Revisar proposta comercial',
 'Ajustar escopo e valores antes do envio ao cliente.',
 'IN_PROGRESS', UTC_TIMESTAMP(6) + INTERVAL 1 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 5 DAY, UTC_TIMESTAMP(6) - INTERVAL 2 HOUR, NULL),

(UUID(), 'Preparar apresentação do trimestre',
 'Consolidar resultados e montar os slides da diretoria.',
 'PENDING', UTC_TIMESTAMP(6) + INTERVAL 3 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 2 DAY, UTC_TIMESTAMP(6) - INTERVAL 6 HOUR, NULL),

(UUID(), 'Corrigir bug no checkout',
 'Investigar falha intermitente na finalização da compra.',
 'IN_PROGRESS', UTC_TIMESTAMP(6) - INTERVAL 6 HOUR, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 7 DAY, UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, NULL),

(UUID(), 'Planejar sprint 42',
 'Definir prioridades e capacidade do próximo ciclo.',
 'PENDING', UTC_TIMESTAMP(6) + INTERVAL 5 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 1 DAY, UTC_TIMESTAMP(6) - INTERVAL 3 HOUR, NULL),

(UUID(), 'Documentar endpoints da API',
 'Descrever autenticação, tarefas e tratamento de erros.',
 'COMPLETED', UTC_TIMESTAMP(6) - INTERVAL 2 DAY, UTC_TIMESTAMP(6) - INTERVAL 3 DAY,
 UTC_TIMESTAMP(6) - INTERVAL 8 DAY, UTC_TIMESTAMP(6) - INTERVAL 3 DAY, NULL),

(UUID(), 'Entrevistar candidato backend',
 'Avaliar experiência com APIs e modelagem de domínio.',
 'COMPLETED', UTC_TIMESTAMP(6) - INTERVAL 4 DAY, UTC_TIMESTAMP(6) - INTERVAL 1 DAY,
 UTC_TIMESTAMP(6) - INTERVAL 9 DAY, UTC_TIMESTAMP(6) - INTERVAL 1 DAY, NULL),

(UUID(), 'Reunião de alinhamento com design',
 'Alinhar entregas da nova navegação com o time de design.',
 'PENDING', UTC_TIMESTAMP(6) + INTERVAL 2 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 1 DAY, UTC_TIMESTAMP(6) - INTERVAL 5 HOUR, NULL),

(UUID(), 'Atualizar dependências do projeto',
 'Atualizar bibliotecas e corrigir avisos de segurança.',
 'CANCELED', UTC_TIMESTAMP(6) + INTERVAL 6 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 4 DAY, UTC_TIMESTAMP(6) - INTERVAL 2 DAY, NULL),

(UUID(), 'Escrever guia de onboarding',
 'Reunir acessos, processos e primeiros passos do time.',
 'PENDING', UTC_TIMESTAMP(6) - INTERVAL 1 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 10 DAY, UTC_TIMESTAMP(6) - INTERVAL 4 HOUR, NULL),

(UUID(), 'Revisar pull request do colega',
 'Revisar legibilidade, testes e cobertura do fluxo de tarefas.',
 'COMPLETED', UTC_TIMESTAMP(6) - INTERVAL 1 DAY, UTC_TIMESTAMP(6) - INTERVAL 26 HOUR,
 UTC_TIMESTAMP(6) - INTERVAL 5 DAY, UTC_TIMESTAMP(6) - INTERVAL 26 HOUR, NULL),

(UUID(), 'Configurar monitoramento de erros',
 'Configurar alertas de erro e painel de latência.',
 'IN_PROGRESS', UTC_TIMESTAMP(6) + INTERVAL 4 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 3 DAY, UTC_TIMESTAMP(6) - INTERVAL 30 MINUTE, NULL),

(UUID(), 'Organizar retrospectiva do time',
 'Levantar melhorias e ações para o próximo ciclo.',
 'COMPLETED', UTC_TIMESTAMP(6) - INTERVAL 6 DAY, UTC_TIMESTAMP(6) - INTERVAL 2 DAY,
 UTC_TIMESTAMP(6) - INTERVAL 12 DAY, UTC_TIMESTAMP(6) - INTERVAL 2 DAY, NULL),

(UUID(), 'Ajustar layout da página inicial',
 'Refinar espaçamentos e hierarquia visual da home.',
 'CANCELED', UTC_TIMESTAMP(6) + INTERVAL 8 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 2 DAY, UTC_TIMESTAMP(6) - INTERVAL 22 HOUR, NULL),

(UUID(), 'Testar fluxo de recuperação de senha',
 'Garantir que o e-mail de recuperação chegue e expire corretamente.',
 'PENDING', UTC_TIMESTAMP(6) + INTERVAL 7 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 1 DAY, UTC_TIMESTAMP(6) - INTERVAL 5 HOUR, NULL),

(UUID(), 'Validar integração com gateway de pagamento',
 'Testar cenários de sucesso, falha e estorno.',
 'IN_PROGRESS', UTC_TIMESTAMP(6) + INTERVAL 4 HOUR, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 4 DAY, UTC_TIMESTAMP(6) - INTERVAL 45 MINUTE, NULL),

(UUID(), 'Revisar política de privacidade',
 'Revisar textos e adequação à LGPD.',
 'PENDING', UTC_TIMESTAMP(6) + INTERVAL 9 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 2 DAY, UTC_TIMESTAMP(6) - INTERVAL 8 HOUR, NULL),

(UUID(), 'Migrar banco para nova instância',
 'Planejar janela de manutenção e rollback.',
 'PENDING', UTC_TIMESTAMP(6) + INTERVAL 10 DAY, NULL,
 UTC_TIMESTAMP(6) - INTERVAL 6 DAY, UTC_TIMESTAMP(6) - INTERVAL 2 DAY, UTC_TIMESTAMP(6) - INTERVAL 2 DAY),

(UUID(), 'Arquivar documentação antiga',
 'Organizar versões antigas e atualizar índices.',
 'COMPLETED', UTC_TIMESTAMP(6) - INTERVAL 3 DAY, UTC_TIMESTAMP(6) - INTERVAL 4 DAY,
 UTC_TIMESTAMP(6) - INTERVAL 10 DAY, UTC_TIMESTAMP(6) - INTERVAL 1 DAY, UTC_TIMESTAMP(6) - INTERVAL 1 DAY);
