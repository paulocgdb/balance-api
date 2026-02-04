-- Tabela: contas

create table if not exists contas (
                                      id uuid primary key,
                                      titular uuid not null,
                                      saldo numeric(15,2) not null,
    moeda varchar(3) not null,
    ultima_atualizacao timestamptz not null,
    versao bigint not null
    );

create index if not exists idx_contas_titular
    on contas (titular);

-- Tabela: transacoes_processadas
create table if not exists transacoes_processadas (
                                                      id_transacao uuid primary key,
                                                      id_conta uuid not null,
                                                      data_processamento timestamptz not null
);

create index if not exists idx_transacoes_processadas_conta
    on transacoes_processadas (id_conta);