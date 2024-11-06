-- 높은 재귀(반복) 횟수를 허용하도록 설정
SET SESSION cte_max_recursion_depth = 1000;

-- boards 테이블에 더미 데이터 삽입
INSERT INTO item (item_name, category, price, quantity, created_at, is_delete)
WITH RECURSIVE cte (n) AS (SELECT 1
                           UNION ALL
                           SELECT n + 1

                           FROM cte
                           WHERE n < 1000 -- 생성하고 싶은 더미 데이터의 개수
)
SELECT CONCAT('Title', LPAD(n, 7, '0'))                 AS item_name,  -- 'Title' 다음에 7자리 숫자로 구성된 제목 생성
       CONCAT('itemName', LPAD(n, 7, '0')) AS category,
       FLOOR(RAND() * (10000 - 100 + 1)) + 100 AS price,
       5000                                             AS quantity,
       TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 3650 + 1) DAY) +
                 INTERVAL FLOOR(RAND() * 86400) SECOND) AS created_at, -- 최근 10년 내의 임의의 날짜와 시간 생성
       FALSE                                            AS is_delete   -- 기본값으로 false 설정
FROM cte;
