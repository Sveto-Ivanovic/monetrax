INSERT INTO user_table (
    user_id,
    user_email,
    password_hash,
    name,
    surname,
    user_name,
    role,
    has_finished_onboarding,
    has_verified_email
)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'system@internal.local',
           'not_a_real_hash_disabled_login',
           'System',
           'Admin',
           'system_admin',
           'SYSTEM',
           true,
           true
       );

INSERT INTO categories_table (user_id, category_type, name, description, is_default)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'INCOME',      'Default Income Category',      'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE',     'Default Expense Category',     'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER',    'Default Transfer Category',    'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT',  'Default Adjustment Category',  'Category that user defaults when deleting category that is still being used', true);

INSERT INTO categories_table (user_id, category_type, name, description, is_default)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Salary Income',       'Regular income from employment or wages', false),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Freelance Income',    'Income from freelance or contract work', false),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Investment Income',   'Dividends, interest, and capital gains', false),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Gift Received',       'Money received as a gift', false),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Refund',              'Refunds and reimbursements received', false),

    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Retail Store Purchases', 'General purchases from retail stores', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Groceries',              'Food and household supplies', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Rent / Mortgage',        'Housing payments', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Utilities',              'Electricity, water, gas, internet, etc.', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Transportation',         'Fuel, public transit, ride-shares, parking', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Dining Out',             'Restaurants, cafes, and takeout', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Entertainment',          'Movies, streaming, hobbies, events', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Healthcare',             'Medical expenses, pharmacy, insurance copays', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Subscriptions',          'Recurring software and service subscriptions', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Insurance',              'Insurance premiums (non-health)', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Education',              'Tuition, courses, books, supplies', false),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Travel',                 'Flights, hotels, vacations', false),

    ('00000000-0000-0000-0000-000000000001', 'TRANSFER', 'Account Transfer',   'Movement of funds between own accounts', false),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER', 'Savings Deposit',    'Transfer into a savings account', false),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER', 'Loan Repayment',     'Transfer made to repay a loan', false),

    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT', 'Balance Correction', 'Manual correction of account balance', false),
    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT', 'Currency Reconciliation', 'Adjustment due to exchange rate or rounding', false);